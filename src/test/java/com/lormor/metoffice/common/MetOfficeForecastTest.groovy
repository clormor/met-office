package com.lormor.metoffice.common;

import com.lormor.metoffice.ws.MetOfficeWebService

import groovy.json.JsonSlurper

class MetOfficeForecastTest extends GroovyTestCase {

    void testHourlyForecast() {
        def slurper = new JsonSlurper()
        BufferedInputStream thing = Thread.currentThread().getContextClassLoader().getResourceAsStream("hourly-forecast.json")
        String gay = thing.getText()
        def result1 = slurper.parseText(gay)
        System.out.println(result1);

        int cheltId = WeatherUtils.getLocationId("Cheltenham")
        List<MetOfficeForecast> hourlyForecasts = MetOfficeWebService.hourlyForecast(cheltId)

        for (MetOfficeForecast f1 : hourlyForecasts) {
            System.out.println(f1);
        }
    }
}
