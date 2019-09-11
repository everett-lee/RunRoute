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
    }

    @Test
    public void testOffsetLondon21k() {
        double runlength = 21000;

        // should produce four coordinates offset by 10500km from the starting coordinates
        double[] res = dc.calcBoundingBox(51.4459, -0.1235, runlength);

        // expected coordinates from google maps
        // min lat 51.398493
        // min lon -0.251917
        // max lat 51.494450
        // max lon 0.031315

        assertEquals(51.351492, res[1], 0.0005); // min lat comparison
        assertEquals(-0.274880, res[0], 0.0005); // min lon comparison
        assertEquals(51.540230, res[3], 0.0005); // max lat comparison
        assertEquals(0.027940, res[2], 0.0005); // max lon comparison
    }

    @Test
    public void testOffsetManchester21k() {
        double runlength = 21000;
        double[] res = dc.calcBoundingBox(53.478200, -2.241380, runlength);

        // expected coordinates from google maps
        // min lat 53.383711
        // min lon -2.400163
        // max lat 53.478200
        // max lon -2.082634

        assertEquals(53.383711, res[1], 0.0005); // min lat comparison
        assertEquals(-2.400163, res[0], 0.0005); // min lon comparison
        assertEquals(53.572485, res[3], 0.0005); // max lat comparison
        assertEquals(-2.082634, res[2], 0.0005); // max lon comparison
    }

    @Test
    public void testOffsetZeroLon5k() {
        double runLength = 5000;
        double[] res = dc.calcBoundingBox(51,0, runLength);


        // expected coordinates from google maps
        // min lat 50.977473
        // min lon -0.035904
        // max lat 51.022463
        // max lon 0.035713

        assertEquals(50.977473, res[1], 0.0005); // min lat comparison
        assertEquals(-0.035904, res[0], 0.0005); // min lon comparison
        assertEquals(51.022463, res[3], 0.0005); // max lat comparison
        assertEquals(0.035713, res[2], 0.0005); // max lon comparison
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