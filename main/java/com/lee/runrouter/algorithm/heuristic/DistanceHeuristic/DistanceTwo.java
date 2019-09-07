package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DistanceTwo {
    DistanceCalculator distanceCalculator;
    private final double RETURN_SCORE_NUMERATOR = 100;

    @Autowired
    public DistanceTwo(@Qualifier("EuclideanCalculator")
                                                       DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }


    public double getScore(Node currentNode, Node selectedNode, Node originNode,
                           double currentRouteLength, double targetDistance) {
        double score = 0;

        double currentDistanceFromOriginNode =
                distanceCalculator.calculateDistance(currentNode, originNode);
        double selectedDistanceFromOriginNode =
                distanceCalculator.calculateDistance(selectedNode, originNode);

        if (currentRouteLength / targetDistance > 0.5) {
            if (currentDistanceFromOriginNode > (targetDistance - currentRouteLength) * 0.30) {
                score = RETURN_SCORE_NUMERATOR / selectedDistanceFromOriginNode;
            }
        }

        return score;
    }
}
