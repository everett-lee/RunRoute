package com.lee.runrouter.bbcalculator;

import org.springframework.stereotype.Component;

/**
 * For (rough) calculations of offset latitude and longitude from
 * the origin position.
 *
 * This is used as an input for the bounding box
 * query in PostGIS. The total size of the bounding box is scaled,
 * so resulting area can be more or less than the run distance squared.
 *
 * Formula sourced from
 * https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
 */
@Component
public class ScaledBBCalculator implements BBCalculator {
    private final double EARTH_RADIUS_M = 6371000;
    private final double SCALE_DOWN = 1;

    public double[] calcBoundingBox(double startLat, double startLon, double runLength) {
        runLength /= 2; // shift coordinates 0.5 * run length in each direction

        double minLon = getMinLon(startLon, startLat, runLength);
        double minLat = getMinLat(startLat, runLength);
        double maxLon = getMaxLon(startLon, startLat, runLength);
        double maxLat = getMaxLat(startLat, runLength);

        return new double[]{minLon,minLat,maxLon,maxLat};
    }

    private double getMaxLat(double startLat, double runLength) {
        double dLat = (runLength * SCALE_DOWN) / EARTH_RADIUS_M;

        return startLat + Math.toDegrees(dLat);
    }

    private double getMinLat(double startLat, double runLength) {
        double dLat = (runLength * SCALE_DOWN) / EARTH_RADIUS_M;

        return startLat - Math.toDegrees(dLat);
    }

    private double getMaxLon(double startLon, double startLat, double runLength) {
        double dLon = (runLength * SCALE_DOWN)
                / (EARTH_RADIUS_M * Math.cos(Math.PI * startLat / 180));

        return startLon + Math.toDegrees(dLon);
    }

    private double getMinLon(double startLon, double startLat, double runLength) {
        double dLon = (runLength * SCALE_DOWN)
                / (EARTH_RADIUS_M * Math.cos(Math.PI * startLat / 180));

        return startLon - Math.toDegrees(dLon);
    }
}
