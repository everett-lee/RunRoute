package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("DirectDistanceHeuristic")
public class DirectDistanceHeuristic implements DistanceFromOriginNodeHeursitic {
    private DistanceCalculator distanceCalculator;

    @Autowired
    public DirectDistanceHeuristic(@Qualifier("EuclideanCalculator")
                                                       DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public double getScore(Node currentNode, Node selectedNode, Node originNode,
                           double currentRouteLength, double targetDistance) {

        double currentDistanceFromOriginNode =
                distanceCalculator.calculateDistance(currentNode, originNode);
        double selectedDistanceFromOriginNode =
                distanceCalculator.calculateDistance(selectedNode, originNode);

        if (selectedDistanceFromOriginNode > currentDistanceFromOriginNode * 1.5) {
            return - 100;
        }
        return 0;
    }
}