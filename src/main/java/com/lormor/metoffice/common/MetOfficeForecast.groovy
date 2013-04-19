package com.lormor.metoffice.common

import com.lormor.metoffice.ws.MetOfficeConstants.CompassDirection
import com.lormor.metoffice.ws.MetOfficeConstants.ForecastType
import com.lormor.metoffice.ws.MetOfficeConstants.Visibility
import com.lormor.metoffice.ws.MetOfficeConstants.Weather


class MetOfficeForecast {
    ForecastType forecastType
    int id
    Date period
    int tempFeelsLike
    int windGust
    int humidity
    int temperature
    Visibility visibility
    CompassDirection wind
    int uv
    Weather weather
    int rainProbability

    String toString() {
        String.format("Forecast[type:%s, location:%d, period:%s, temperature:%d, feelsLike:%d, wind:%s]", forecastType.toString(), id, period.toString(), temperature, tempFeelsLike, wind.toString())
    }
}
