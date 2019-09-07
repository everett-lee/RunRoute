package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Calculates a score based on the current distance
 * from the origin point. This is used to favour routes
 * that move back towards the starting point once the
 * route reaches a certain distance
 */
@Component
@Qualifier("DistanceFromOriginNodeHeuristicMain")
public class DistanceFromOriginNodeHeuristicMain implements DistanceFromOriginNodeHeursitic {
    private DistanceCalculator distanceCalculator;
    private final double RETURN_SCORE_NUMERATOR = 100;

    @Autowired
    public DistanceFromOriginNodeHeuristicMain(@Qualifier("EuclideanCalculator")
                                                       DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
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