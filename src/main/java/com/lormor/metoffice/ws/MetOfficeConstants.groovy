package com.lormor.metoffice.ws

import java.text.SimpleDateFormat

import groovy.json.JsonSlurper
import wslite.rest.RESTClient

class MetOfficeConstants {

    static final String MET_OFFICE_HOST="http://datapoint.metoffice.gov.uk"
    static final String MET_OFFICE_SERVICE_BASE="public/data/val/wxfcs/all/json"
    static final String MET_OFFICE_KEY="239bc66d-2507-4711-a7bb-93e43af3169e"
    static final SimpleDateFormat MET_OFFICE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    static final SimpleDateFormat MET_OFFICE_PERIOD_FORMAT = new SimpleDateFormat("yyyy-MM-dd'Z'")

    static webServiceRequest() {
        def client = new RESTClient(MET_OFFICE_HOST)
        def response = client.get(path:"${MET_OFFICE_SERVICE_BASE}/sitelist", query:[key:"${MET_OFFICE_KEY}"])
        assert 200 == response.statusCode
        def slurper = new JsonSlurper()

        return slurper.parseText(response.contentAsString)
    }

    enum ForecastType {
        DAILY, HOURLY
    }

    enum Visibility {
        UNKNOWN, VERY_POOR, POOR, MODERATE, GOOD, VERY_GOOD, EXCELLENT
    }

    enum CompassDirection {
        UNKNOWN, N, NE, NNE, ENE, E, ESE, SE, SSE, S, SSW, SW, WSW, W, WNW, NNW, NW
    }

    enum Weather {
        NOT_AVAILABLE,
        CLEAR_NIGHT,
        SUNNY_DAY,
        PARTLY_CLOUDY_NIGHT,
        PARTLY_CLOUDY_DAY,
        NOT_USED,
        MIST,
        FOG,
        CLOUDY,
        OVERCAST,
        LIGHT_RAIN_NIGHT,
        LIGHT_RAIN_DAY,
        DRIZZLE,
        LIGHT_RAIN,
        HEAVY_RAIN_NIGHT,
        HEAVY_RAIN_DAY,
        HEAVY_RAIN,
        SLEET_NIGHT,
        SLEET_DAY,
        SLEET,
        HAIL_NIGHT,
        HAIL_DAY,
        HAIL,
        LIGHT_SNOW_NIGHT,
        LIGHT_SNOW_DAY,
        LIGHT_SNOW,
        HEAVY_SNOW_NIGHT,
        HEAVY_SNOW_DAY,
        HEAVY_SNOW,
        THUNDER_NIGHT,
        THUNDER_DAY,
        THUNDER
    }
}
