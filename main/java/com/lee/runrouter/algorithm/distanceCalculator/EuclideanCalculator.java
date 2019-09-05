package com.lee.runrouter.algorithm.distanceCalculator;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/***
 * Approximates distance along the Earth's surface 'as the crow flies'
 * using an equirectangular approximation formula.
 * Sourced from https://www.movable-type.co.uk/scripts/latlong.html
 */
@Component
@Qualifier("EuclideanCalculator")
public class EuclideanCalculator implements DistanceCalculator {
    private final double EARTH_RADIUS_METRES = 6371000;

    /**
     *
     * @param n1 First graph Node to measure distance from
     * @param n2 Second Graph node to measure distance to
     * @return an approximately in of the distance between the two
     *         points in metres.
     */
    @Override
    public double calculateDistance(Node n1, Node n2) {
        Double lat1Radians = Math.toRadians(n1.getLat());
        Double lat2Radians = Math.toRadians(n2.getLat());
        Double lon1Radians = Math.toRadians(n1.getLon());
        Double lon2Radians = Math.toRadians(n2.getLon());

        double deltaLat = lat2Radians - lat1Radians;
        double deltaLon = lon2Radians - lon1Radians;
        double meanLat = (lat1Radians + lat2Radians)/2;

        double x  = deltaLon * Math.cos(meanLat);
        double y = deltaLat;

        return Math.sqrt(Math.pow(x,2) + Math.pow(y, 2)) * EARTH_RADIUS_METRES;
    }
}
