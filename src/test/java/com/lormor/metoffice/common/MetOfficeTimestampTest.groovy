package com.lormor.metoffice.common;

import com.lormor.metoffice.common.WeatherUtils

class MetOfficeTimestampTest extends GroovyTestCase {

    void testTimeStamps() {
        List<Date> allDates = WeatherUtils.getNextTimestamps(0)
        List<Date> someDates = WeatherUtils.getNextTimestamps(10)

        assertTrue(44 < allDates.size())
        assertTrue(51 > allDates.size())
        assertEquals(10, someDates.size())
    }
}
