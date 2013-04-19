package com.lormor.metoffice.common


import com.lormor.metoffice.common.MetOfficeLocation;
import com.lormor.metoffice.common.WeatherUtils


class MetOfficeLocationTest extends GroovyTestCase {

    void testClosest() {
        compareClosest("Cheltenham", 51.906353, -2.105277)
        compareClosest("Gloucester", 51.853276,-2.240353)
        compareClosest("Trefonen", 52.829321,-3.581543)
        compareClosest("Deanburnhaugh", 55.37911,-2.856445)
        compareClosest("Cookstown", 54.914514,-6.745605)
        compareClosest("Aylsham", 52.802761,1.208496)
        compareClosest("Battle", 50.916887,0.681152)
        compareClosest("Draynes", 50.499452,-4.526367)
    }

    private void compareClosest(String expected, float lat, float lon) {
        assertEquals(expected, WeatherUtils.findClosestLocation(lat,lon).getName())
    }
}