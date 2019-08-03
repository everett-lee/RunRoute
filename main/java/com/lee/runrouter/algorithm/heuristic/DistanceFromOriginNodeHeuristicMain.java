package com.lee.runrouter.algorithm.heuristic;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Calculates a score based on the current distance
 * from the origin point. This is used to favour routes
 * that move further from the origin in the first half
 * of the route and move closer to the start in the second
 * halfs
 */
@Component
@Qualifier("DistanceFromOriginNodeHeuristicMain")
public class DistanceFromOriginNodeHeuristicMain implements DistanceFromOriginNodeHeursitic {
    private DistanceCalculator distanceCalculator;
    private final double SWITCH_PERCENTAGE = 0.5; // the percentage of the route at
    // which the heuristic switches from favouring outward paths to favouring returning
    // paths
    private final double RETURN_SCORE_NUMERATOR = 1500.0;
    private final double OUTWARD_SCORE_DENOMINATOR = 10000.0;

    @Autowired
    public DistanceFromOriginNodeHeuristicMain(@Qualifier("HaversineCalculator")
                                               DistanceCalculator distanceCalculator) {

        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public double getScore(Node currentNode, Node originNode,
                           double currentRouteLength, double targetDistance) {
        double score = 0;

        double distanceFromOriginNode =
                HaversineCalculator.calculateDistanceII(currentNode, originNode);

        if (currentRouteLength > targetDistance * SWITCH_PERCENTAGE) {
            score += RETURN_SCORE_NUMERATOR / distanceFromOriginNode;
        } else {
            score += distanceFromOriginNode / OUTWARD_SCORE_DENOMINATOR;
        }

        return score;
    }
}
