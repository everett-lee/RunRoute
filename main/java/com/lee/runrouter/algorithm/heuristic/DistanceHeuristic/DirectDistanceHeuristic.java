package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Compares the distance 'as the crow flies' between two Nodes and the target,
 * and returns a negative score if the second Node is more than some
 * percentages of the first Node's distance away from the target. This guides
 * the search towards the goal.
 */
@Component
@Qualifier("DirectDistanceHeuristic")
public class DirectDistanceHeuristic implements DistanceFromOriginNodeHeursitic {
    private final double SCALE_UP = 2; // amount to scale up the current Node's
    // distance by
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

        if (selectedDistanceFromOriginNode >
                currentDistanceFromOriginNode * SCALE_UP) {
            return - 100;
        }
        return 0;
    }
}