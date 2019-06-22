package com.lee.runrouter.BBCalculator;

import org.springframework.stereotype.Component;

/**
 * For (rough) calculations of offset latitude and longitude from
 * the origin position. This is used as an input for the bounding box
 * query in PostGIS. The total size of the bounding box scaled down,
 * so contained area < run distance ^ 2.
 */
@Component
public class ScaledBBCalculator implements BBCalculator {
    private final double EARTH_RADIUS_KM = 6371;
    private final double PI = Math.PI;
    private final double SCALE_DOWN = 0.8;

    public double[] calcBoundingBox(double startLat, double startLon, double runLength) {
        double minLon = getMinLon(startLon, startLat, runLength/2);
        double minLat = getMinLat(startLat, runLength/2);
        double maxLon = getMaxLon(startLon, startLat, runLength/2);
        double maxLat = getMaxLat(startLat, runLength/2);

        return new double[]{minLon,minLat,maxLon,maxLat};
    }

    private double getMaxLat(double startLat, double runLength) {
        return startLat + ((runLength * SCALE_DOWN) / EARTH_RADIUS_KM) * (180/PI);
    }

    private double getMinLat(double startLat, double runLength) {
        return startLat - ((runLength * SCALE_DOWN) / EARTH_RADIUS_KM) * (180/PI);
    }

    private double getMaxLon(double startLon, double startLat, double runLength) {
        return startLon + (((runLength * SCALE_DOWN) / EARTH_RADIUS_KM)) * (180/PI)
                / Math.cos(startLat * (PI/180));
    }

    private double getMinLon(double startLon, double startLat, double runLength) {
        return startLon - ((runLength * SCALE_DOWN) / EARTH_RADIUS_KM) * (180/PI)
                / Math.cos(startLat * (PI/180));
    }
}
