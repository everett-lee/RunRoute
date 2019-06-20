package com.lee.runrouter.distancecalc;

import org.springframework.stereotype.Component;

@Component
public interface BBCalculator {
    public double[] calcBoundingBox(double startLat, double startLon, double runLength);
}
