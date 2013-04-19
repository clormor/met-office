package com.lormor.metoffice.ws;

import com.lormor.metoffice.common.MetOfficeForecast
import com.lormor.metoffice.common.MetOfficeLocation
import com.lormor.metoffice.common.WeatherUtils

import groovy.json.JsonSlurper
import wslite.rest.*

class MetOfficeWebService {

    static parseSiteList(Object siteList) {
        def locations = siteList.Locations.Location;
        Collection<MetOfficeLocation> allLocations = new HashSet<MetOfficeLocation>();

        locations.each {
            MetOfficeLocation location = new MetOfficeLocation(id: Integer.parseInt(it.id), name: it.name, lat: Float.parseFloat(it.latitude), lon: Float.parseFloat(it.longitude));
            allLocations.add(location);
        }
    }

    static Collection<MetOfficeLocation> siteList() {
        def result = MetOfficeConstants.webServiceRequest()
        return parseSiteList(result)
    }

    static List<String> availableHourlyTimestamps() {
        def client = new RESTClient(MetOfficeConstants.MET_OFFICE_HOST)
        def response = client.get(path:"${MetOfficeConstants.MET_OFFICE_SERVICE_BASE}/capabilities", query:[res:"3hourly", key:"${MetOfficeConstants.MET_OFFICE_KEY}"])
        List<String> allTimestamps = new ArrayList<String>();

        assert 200 == response.statusCode

        def slurper = new JsonSlurper()
        def result = slurper.parseText(response.contentAsString)
        def timezones = result.Resource.TimeSteps.TS;

        timezones.each { allTimestamps.add(it) }
    }

    static List<String> availableDailyTimestamps() {
        def client = new RESTClient(MetOfficeConstants.MET_OFFICE_HOST)
        def response = client.get(path:"${MetOfficeConstants.MET_OFFICE_SERVICE_BASE}/capabilities", query:[res:"daily", key:"${MetOfficeConstants.MET_OFFICE_KEY}"])
        List<String> allTimestamps = new ArrayList<String>();

        assert 200 == response.statusCode

        def slurper = new JsonSlurper()
        def result = slurper.parseText(response.contentAsString)
        def timezones = result.Resource.TimeSteps.TS;

        timezones.each { allTimestamps.add(it) }
    }

    static List<MetOfficeForecast> hourlyForecast(int locationId) {
        def client = new RESTClient(MetOfficeConstants.MET_OFFICE_HOST)
        def response = client.get(path:"${MetOfficeConstants.MET_OFFICE_SERVICE_BASE}/${locationId}", query:[res:"3hourly", key:"${MetOfficeConstants.MET_OFFICE_KEY}"])
        List<String> allTimestamps = new ArrayList<String>();
        List<MetOfficeForecast> result = new ArrayList<MetOfficeForecast>();

        assert 200 == response.statusCode

        def slurper = new JsonSlurper()
        def content = slurper.parseText(response.contentAsString)
        def location = content.SiteRep.DV.Location

        def period = location.Period

        period.each { p ->
            def weather = p.Rep

            weather.each { w ->

                MetOfficeForecast forecast = new MetOfficeForecast(
                        forecastType: MetOfficeConstants.ForecastType.HOURLY,
                        id: Integer.parseInt(location.i),
                        tempFeelsLike: Integer.parseInt(w.F),
                        windGust: Integer.parseInt(w.G),
                        humidity: Integer.parseInt(w.H),
                        temperature: Integer.parseInt(w.T),
                        uv: Integer.parseInt(w.U),
                        rainProbability: Integer.parseInt(w.Pp),
                        period: WeatherUtils.convertPeriod(p.value, w.$),
                        visibility: WeatherUtils.convertVisibility(w.V),
                        wind: WeatherUtils.convertWind(w.D),
                        weather: WeatherUtils.convertWeather(w.W)
                        )

                result.add(forecast)
            }
        }

        return result
    }

    static List<MetOfficeForecast> dailyForecast(int locationId) {
        def client = new RESTClient(MetOfficeConstants.MET_OFFICE_HOST)
        def response = client.get(path:"${MetOfficeConstants.MET_OFFICE_SERVICE_BASE}/${locationId}", query:[res:"daily", key:"${MetOfficeConstants.MET_OFFICE_KEY}"])
        List<String> allTimestamps = new ArrayList<String>();
        List<MetOfficeForecast> result = new ArrayList<MetOfficeForecast>();

        assert 200 == response.statusCode

        def slurper = new JsonSlurper()
        def content = slurper.parseText(response.contentAsString)
        def location = content.SiteRep.DV.Location

        def period = location.Period

        period.each { p ->
            def weather = p.Rep

            weather.each { w ->

                MetOfficeForecast forecast = new MetOfficeForecast(
                        forecastType: MetOfficeConstants.ForecastType.DAILY,
                        id: Integer.parseInt(location.i),
                        tempFeelsLike: Integer.parseInt(w.F),
                        windGust: Integer.parseInt(w.G),
                        humidity: Integer.parseInt(w.H),
                        temperature: Integer.parseInt(w.T),
                        uv: Integer.parseInt(w.U),
                        rainProbability: Integer.parseInt(w.Pp),
                        period: WeatherUtils.convertPeriod(p.value, w.$),
                        visibility: WeatherUtils.convertVisibility(w.V),
                        wind: WeatherUtils.convertWind(w.G),
                        weather: WeatherUtils.convertWeather(w.W)
                        )

                result.add(forecast)
            }
        }

        System.out.println("----");
        System.out.println(content);
        System.out.println("----");

        return result
    }

    static MetOfficeForecast dailyForecast(int locationId, Date date) {
        String timeStamp = WeatherUtils.getDailyTimeStamp(date)
        return dailyForecastByTimeStamp(locationId, timeStamp)
    }

    static List<MetOfficeForecast> hourlyForecast(int locationId, Date date) {
        List<String> timeStamps = WeatherUtils.getHourlyTimeStamp(date)
        List<MetOfficeForecast> result = new ArrayList<MetOfficeForecast>();

        for (String timeStamp : timeStamps) {
            hourlyForecastByTimeStamp(locationId, timeStamp)
        }
    }

    private static MetOfficeForecast hourlyForecastByTimeStamp(int locationId, String timeStamp) {
        def client = new RESTClient(MetOfficeConstants.MET_OFFICE_HOST)
        def response = client.get(path:"${MetOfficeConstants.MET_OFFICE_SERVICE_BASE}/${locationId}", query:[res:"3hourly", key:"${MetOfficeConstants.MET_OFFICE_KEY}", time:"${timeStamp}"])
        List<String> allTimestamps = new ArrayList<String>();
        List<MetOfficeForecast> result = new ArrayList<MetOfficeForecast>();

        assert 200 == response.statusCode

        def slurper = new JsonSlurper()
        def content = slurper.parseText(response.contentAsString)

        System.out.println("----");
        System.out.println(content);
        System.out.println("----");

        def location = content.SiteRep.DV.Location

        def period = location.Period

        period.each { p ->
            def weather = p.Rep

            weather.each { w ->

                MetOfficeForecast forecast = new MetOfficeForecast(
                        forecastType: MetOfficeConstants.ForecastType.HOURLY,
                        id: Integer.parseInt(location.i),
                        tempFeelsLike: Integer.parseInt(w.F),
                        windGust: Integer.parseInt(w.G),
                        humidity: Integer.parseInt(w.H),
                        temperature: Integer.parseInt(w.T),
                        uv: Integer.parseInt(w.U),
                        rainProbability: Integer.parseInt(w.Pp),
                        period: WeatherUtils.convertPeriod(p.value, w.$),
                        visibility: WeatherUtils.convertVisibility(w.V),
                        wind: WeatherUtils.convertWind(w.G),
                        weather: WeatherUtils.convertWeather(w.W)
                        )

                result.add(forecast)
            }
        }

        return (result.isEmpty()) ? null : result.get(0)
    }

    private static MetOfficeForecast dailyForecastByTimeStamp(int locationId, String timeStamp) {
        def client = new RESTClient(MetOfficeConstants.MET_OFFICE_HOST)
        def response = client.get(path:"${MetOfficeConstants.MET_OFFICE_SERVICE_BASE}/${locationId}", query:[res:"daily", key:"${MetOfficeConstants.MET_OFFICE_KEY}", time:"${timeStamp}"])
        List<String> allTimestamps = new ArrayList<String>();
        List<MetOfficeForecast> result = new ArrayList<MetOfficeForecast>();

        assert 200 == response.statusCode

        def slurper = new JsonSlurper()
        def content = slurper.parseText(response.contentAsString)
        def location = content.SiteRep.DV.Location

        def period = location.Period

        period.each { p ->
            def weather = p.Rep

            weather.each { w ->

                MetOfficeForecast forecast = new MetOfficeForecast(
                        forecastType: MetOfficeConstants.ForecastType.DAILY,
                        id: Integer.parseInt(location.i),
                        tempFeelsLike: Integer.parseInt(w.F),
                        windGust: Integer.parseInt(w.G),
                        humidity: Integer.parseInt(w.H),
                        temperature: Integer.parseInt(w.T),
                        uv: Integer.parseInt(w.U),
                        rainProbability: Integer.parseInt(w.Pp),
                        period: WeatherUtils.convertPeriod(p.value, w.$),
                        visibility: WeatherUtils.convertVisibility(w.V),
                        wind: WeatherUtils.convertWind(w.G),
                        weather: WeatherUtils.convertWeather(w.W)
                        )

                result.add(forecast)
            }
        }

        System.out.println("----");
        System.out.println(content);
        System.out.println("----");

        return (result.isEmpty()) ? null : result.get(0)
    }
}
