package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *  simply returns the the current distance from the origin node. Used to trim
 *  branches in the connection path algorithm
 */
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
    public double getScore(Node currentNode, Node targetNode, double currentRouteLength, double targetDistance) {
        return distanceCalculator.calculateDistance(currentNode, targetNode);
    }
}
