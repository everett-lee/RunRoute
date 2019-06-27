package com.lee.runrouter.bbcalculator;

import org.springframework.stereotype.Component;

@Component
public interface BBCalculator {
    public double[] calcBoundingBox(double startLat, double startLon, double runLength);
}
