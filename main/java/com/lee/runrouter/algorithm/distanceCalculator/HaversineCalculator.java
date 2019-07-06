package com.lee.runrouter.algorithm.distanceCalculator;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.stereotype.Component;

/***
 * Approximates distance along the Earth's surface 'as the crow flies'
 * using the Haversine formula.
 * Sourced from https://www.movable-type.co.uk/scripts/latlong.html
 */
@Component
public class HaversineCalculator implements DistanceCalculator {
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
        double lat1 = Math.toRadians(n1.getLat());
        double lat2 = Math.toRadians(n2.getLat());
        double deltaLat = Math.toRadians(n2.getLat() - n1.getLat());
        double deltaLon = Math.toRadians(n2.getLon() - n1.getLon());

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon/2) * Math.sin(deltaLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS_METRES * c;
    }
}
