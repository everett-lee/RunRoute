package com.lee.runrouter.bbcalculator;

import org.springframework.stereotype.Component;

/**
 * Interface for calculating the bounding coordinates for
 * the spatial database query
 */
@Component
public interface BBCalculator {
    double[] calcBoundingBox(double startLat, double startLon, double runLength);
}
