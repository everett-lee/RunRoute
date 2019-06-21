package com.lee.runrouter.distancecalc;

import org.junit.*;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ScaledBBCalculatorTest {
    ScaledBBCalculator dc;

    @Before
    public void setUp() {
        dc = new ScaledBBCalculator();
    }

    @Test
    public void testOffsetOne() {
        double[] res = dc.calcBoundingBox(51.445,-0.124, 5);

        assertEquals(res[0], -0.15285, 0.0001);
        assertEquals(res[1], 51.42701, 0.0001);
        assertEquals(res[2], -0.09514, 0.0001);
        assertEquals(res[3], 51.46298, 0.0001);
    }

    @Test
    public void testOffsetZeroLon() {
        double[] res = dc.calcBoundingBox(50,0, 10);

        assertEquals(res[0], -0.05596, 0.0001);
        assertEquals(res[1], 49.9640, 0.0001);
        assertEquals(res[2], 0.05596, 0.0001);
        assertEquals(res[3],  50.03597, 0.0001);
    }
}