package com.lee.runrouter.bbcalculator;

import org.springframework.stereotype.Component;

/**
 * For (rough) calculations of offset latitude and longitude from
 * the origin position. This is used as an input for the bounding box
 * query in PostGIS. The total size of the bounding box scaled down,
 * so resulting area is less than run distance squared.
 */
@Component
public class ScaledBBCalculator implements BBCalculator {
    private final double EARTH_RADIUS_M = 6371000;
    private final double SCALE_DOWN = 0.65;

    public double[] calcBoundingBox(double startLat, double startLon, double runLength) {
        double minLon = getMinLon(startLon, startLat, runLength/2);
        double minLat = getMinLat(startLat, runLength/2);
        double maxLon = getMaxLon(startLon, startLat, runLength/2);
        double maxLat = getMaxLat(startLat, runLength/2);

        return new double[]{minLon,minLat,maxLon,maxLat};
    }

    private double getMaxLat(double startLat, double runLength) {
        return startLat + Math.toDegrees((runLength * SCALE_DOWN) / EARTH_RADIUS_M);
    }

    private double getMinLat(double startLat, double runLength) {
        return startLat - Math.toDegrees((runLength * SCALE_DOWN) / EARTH_RADIUS_M);
    }

    private double getMaxLon(double startLon, double startLat, double runLength) {
        return startLon + Math.toDegrees(((runLength * SCALE_DOWN) / EARTH_RADIUS_M))
                / Math.cos(Math.toRadians(startLat));
    }

    private double getMinLon(double startLon, double startLat, double runLength) {
        return startLon - Math.toDegrees((runLength * SCALE_DOWN) / EARTH_RADIUS_M)
                / Math.cos(Math.toRadians(startLat));
    }
}
