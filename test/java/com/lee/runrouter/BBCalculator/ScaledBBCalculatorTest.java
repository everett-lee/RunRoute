package com.lee.runrouter.BBCalculator;

import com.lee.runrouter.bbcalculator.ScaledBBCalculator;
import org.junit.*;
import java.lang.reflect.*;

import static org.junit.Assert.*;

public class ScaledBBCalculatorTest {
    ScaledBBCalculator dc;
    double scaleDown;
    final double EARTH_RADIUS_M = 6371000;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        dc = new ScaledBBCalculator();

        Field f = dc.getClass().getDeclaredField("SCALE_DOWN");
        f.setAccessible(true);
        scaleDown = (double) f.get(dc);
    }

    @Test
    public void testOffsetOne() {
        double runlength = 5;
        double[] res = dc.calcBoundingBox(51.4,-0.12, runlength);

        double minLon = -0.12 - ((((runlength * scaleDown)) /
                (EARTH_RADIUS_M * (Math.cos(Math.PI * 51.4/180)))) * (180/Math.PI));
        double minLat = 51.4 - (((runlength * scaleDown) / EARTH_RADIUS_M) * (180/Math.PI));
        double maxLon = -0.12 + ((((runlength * scaleDown)) /
                (EARTH_RADIUS_M * (Math.cos(Math.PI * 51.4/180)))) * (180/Math.PI));
        double maxLat = 51.4 + (((runlength * scaleDown) / EARTH_RADIUS_M) * (180/Math.PI));

        assertEquals(minLon, res[0], 0.001);
        assertEquals(minLat, res[1], 0.001);
        assertEquals(maxLon, res[2], 0.001);
        assertEquals(maxLat, res[3], 0.001);
    }

    @Test
    public void testOffsetZeroLon() {
        double runLength = 2000;
        double lat = 50;
        double lon = 0;
        double[] res = dc.calcBoundingBox(50,0, runLength);

        double dLat = (runLength/2 * scaleDown) / EARTH_RADIUS_M;
        double dLon = (runLength/2 * scaleDown) / (EARTH_RADIUS_M * Math.cos(Math.PI * lat / 180));

        double minLon = lon - (dLon * 180/Math.PI);
        double minLat = lat - (dLat * 180/Math.PI);
        double maxLon = lon + (dLon * 180/Math.PI);
        double maxLat = lat + (dLat * 180/Math.PI);

        assertEquals(minLon, res[0], 0.001);
        assertEquals(minLat, res[1], 0.001);
        assertEquals(maxLon, res[2], 0.001);
        assertEquals(maxLat, res[3], 0.001);
    }

    @Test
    public void testForGivenInputs() throws NoSuchFieldException, IllegalAccessException {
        Field f = dc.getClass().getDeclaredField("EARTH_RADIUS_M");
        f.setAccessible(true);
        f.set(dc, 6378137);
        f = dc.getClass().getDeclaredField("SCALE_DOWN");
        f.setAccessible(true);
        f.set(dc, 1);

        double runLength = 200;
        double lat = 51;
        double lon = 0;
        double[] res = dc.calcBoundingBox(lat,lon, runLength);

        double maxLat = 51.00089;
        double maxLon = 0.001427;
        double dLat = maxLat - lat;
        double dLon = maxLon - lon;
        double minLat = lat - dLat;
        double minLon = lon - dLon;

        assertEquals(minLon, res[0], 0.001);
        assertEquals(minLat, res[1], 0.001);
        assertEquals(maxLon, res[2], 0.001);
        assertEquals(maxLat, res[3], 0.001);
    }
}