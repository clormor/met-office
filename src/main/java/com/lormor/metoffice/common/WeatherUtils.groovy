package com.lormor.metoffice.common

import com.lormor.metoffice.ws.MetOfficeConstants
import com.lormor.metoffice.ws.MetOfficeWebService
import com.lormor.metoffice.ws.MetOfficeConstants.CompassDirection
import com.lormor.metoffice.ws.MetOfficeConstants.Visibility
import com.lormor.metoffice.ws.MetOfficeConstants.Weather

class WeatherUtils {

    private static Collection<MetOfficeLocation> locations
    private static Collection<String> hourlyTimestamps
    private static Collection<String> dailyTimestamps

    // an in-memory index of location names, initialized lazily
    private static Map<String, Integer> locationMap = new HashMap<String, Integer>()

    static {
        // initialize data at run-time, then update periodically
        locations = MetOfficeWebService.siteList()
        hourlyTimestamps = MetOfficeWebService.availableHourlyTimestamps()
        dailyTimestamps = MetOfficeWebService.availableDailyTimestamps()
    }

    private static void initialiseLocationMap() {
        if (locationMap.isEmpty()) {
            for (MetOfficeLocation location : locations) {
                locationMap.put(location.name, location.id)
            }
        }
    }

    static Date parseMetOfficeDate(String date) {
        return MetOfficeConstants.MET_OFFICE_DATE_FORMAT.parse(date)
    }

    static MetOfficeLocation findClosestLocation(float lat, float lon) {
        float closest = Float.MAX_VALUE
        MetOfficeLocation result = null

        // determine proximity to geo point by using c^2 = a^2 * b^2
        locations.each {
            float distance = Math.sqrt((Math.pow(lat - it.lat,2)) * (Math.pow(lon - it.lon,2)))

            if (distance < closest) {
                closest = distance
                result = it
            }
        }

        return result
    }

    static List<String> getNextTimestamps() {
        // default, get all available time stamps
        return getNextTimestamps(0)
    }

    static List<String> getNextTimestamps(int limit) {
        int beginIndex = findFirstTimeStamp()

        // ignore invalid inputs
        if (limit < 0 ) {
            limit = 0
        }

        if (limit == 0) {
            // 0 is default for 'return all dates'
            return hourlyTimestamps
        } else {
            // return the number of values specified by limit
            return hourlyTimestamps[beginIndex..(beginIndex + limit - 1)]
        }
    }

    private static int findFirstTimeStamp() {
        // use a default date of 'now'
        findFirstTimeStamp(new Date())
    }

    private static int findFirstTimeStamp(Date date) {
        int result = 0

        for (String timeStamp : hourlyTimestamps) {
            Date ts = parseMetOfficeDate(timeStamp)

            if (date.compareTo(ts) > 0) {
                // if the time stamp occurs in the past, move on
                result++
            } else {
                // the first time stamp that is not in the past, return
                return result
            }
        }
    }

    static int getLocationId(String locationName) {

        // using the map searches for names more efficiently
        if (locationMap.isEmpty()) {
            initialiseLocationMap()
        }

        return locationMap.get(locationName)
    }

    static int getLocationId(float lat, float lon) {
        WeatherUtils.findClosestLocation(lat, lon).getId();
    }

    static String getDailyTimeStamp(Date date) {
        date.clearTime()

        // return any date which matches the input - ignoring time of day
        for (String timeStamp : dailyTimestamps) {
            Date compareTo = parseMetOfficeDate(timeStamp)
            compareTo.clearTime()

            if (date.compareTo(compareTo) == 0) {
                return timeStamp
            }
        }

        return ""
    }

    static List<String> getHourlyTimeStamp(Date date) {
        List<String> result = new ArrayList<String>()
        date.clearTime()

        // return all dates matching the input - ignoring time of day
        for (String timeStamp : hourlyTimestamps) {
            Date compareTo = parseMetOfficeDate(timeStamp)
            compareTo.clearTime()

            if (date.compareTo(compareTo) == 0) {
                result.add(timeStamp)
            }
        }

        return result
    }

    static Date convertPeriod(String period, String minutesPastMidnight) {
        Date d = MetOfficeConstants.MET_OFFICE_PERIOD_FORMAT.parse(period)
        d.clearTime()
        Calendar c = d.toCalendar()
        c.add(Calendar.MINUTE, Integer.parseInt(minutesPastMidnight))
        return c.time
    }

    static Visibility convertVisibility(String visibility) {
        switch(visibility) {
            case "UN":
                return Visibility.UNKNOWN
            case "VP":
                return Visibility.VERY_POOR
            case "PO":
                return Visibility.POOR
            case "MO":
                return Visibility.MODERATE
            case "GO":
                return Visibility.GOOD
            case "VG":
                return Visibility.VERY_GOOD
            case "EX":
                return Visibility.EXCELLENT
            default:
                return Visibility.UNKNOWN
        }
    }

    static CompassDirection convertWind(String wind) {
        switch(wind) {
            case "N":
                return CompassDirection.N
            case "NNE":
                return CompassDirection.NNE
            case "ENE":
                return CompassDirection.ENE
            case "E":
                return CompassDirection.E
            case "NE":
                return CompassDirection.NE
            case "NW":
                return CompassDirection.NW
            case "ESE":
                return CompassDirection.ESE
            case "SSE":
                return CompassDirection.SSE
            case "S":
                return CompassDirection.S
            case "SSW":
                return CompassDirection.SSW
            case "WSW":
                return CompassDirection.WSW
            case "W":
                return CompassDirection.W
            case "SW":
                return CompassDirection.SW
            case "SE":
                return CompassDirection.SE
            case "WNW":
                return CompassDirection.WNW
            case "NNW":
                return CompassDirection.NNW
            default:
                return CompassDirection.UNKNOWN
        }
    }

    static Weather convertWeather(String weather) {
        int weatherInt = Integer.parseInt(weather)

        if (weather == "NA")
            return Weather.NOT_AVAILABLE

        switch(weatherInt) {
            case 0:
                return Weather.CLEAR_NIGHT
            case 1:
                return Weather.SUNNY_DAY
            case 2:
                return Weather.PARTLY_CLOUDY_NIGHT
            case 3:
                return Weather.PARTLY_CLOUDY_DAY
            case 4:
                return Weather.NOT_USED
            case 5:
                return Weather.MIST
            case 6:
                return Weather.FOG
            case 7:
                return Weather.CLOUDY
            case 8:
                return Weather.OVERCAST
            case 9:
                return Weather.LIGHT_RAIN_NIGHT
            case 10:
                return Weather.LIGHT_RAIN_DAY
            case 11:
                return Weather.DRIZZLE
            case 12:
                return Weather.LIGHT_RAIN
            case 13:
                return Weather.HEAVY_RAIN_NIGHT
            case 14:
                return Weather.HEAVY_RAIN_DAY
            case 15:
                return Weather.HEAVY_RAIN
            case 16:
                return Weather.SLEET_NIGHT
            case 17:
                return Weather.SLEET_DAY
            case 18:
                return Weather.SLEET
            case 19:
                return Weather.HAIL_NIGHT
            case 20:
                return Weather.HAIL_DAY
            case 21:
                return Weather.HAIL
            case 22:
                return Weather.LIGHT_SNOW_NIGHT
            case 23:
                return Weather.LIGHT_SNOW_DAY
            case 24:
                return Weather.LIGHT_SNOW
            case 25:
                return Weather.HEAVY_SNOW_NIGHT
            case 26:
                return Weather.HEAVY_SNOW_DAY
            case 27:
                return Weather.HEAVY_SNOW
            case 28:
                return Weather.THUNDER_NIGHT
            case 29:
                return Weather.THUNDER_DAY
            case 30:
                return Weather.THUNDER
            default:
                return Weather.NOT_AVAILABLE
        }
    }
}
