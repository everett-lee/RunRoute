package com.lee.runrouter.algorithm.distanceCalculator;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.stereotype.Component;

@Component
/**
 * Provides an interface for measuring distance between
 * two pairs of coordinates, as contained in the Node inputs.
 */
public interface DistanceCalculator {
    public double calculateDistance(Node n1, Node n2);
}
