package com.lee.runrouter.algorithm.distanceCalculator;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.stereotype.Component;

@Component
public interface DistanceCalculator {
    public double calculateDistance(Node n1, Node n2);
}
