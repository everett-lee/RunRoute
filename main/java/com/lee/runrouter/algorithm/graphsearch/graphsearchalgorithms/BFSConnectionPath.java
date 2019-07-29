package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ConnectionPair;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Variant of the BFS algorithm that restricts the next selected Way
 * to those closer to the starting point than the previous. It is used
 * to complete the circuit and return to the route's starting position
 * following execution of the BFS.
 */
@Component
@Qualifier("BFSConnectionPath")
public class BFSConnectionPath extends SearchAlgorithm implements ILSGraphSearch {
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 500; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 1;
    private final double PREFERRED_LENGTH = 1100;
    private final double PREFERRED_LENGTH_BONUS = 1;
    private final double DISTANCE_FROM_ORIGIN_BONUS = 0.725;
    private final long TIME_LIMIT = 1000;

    private PriorityQueue<PathTuple> queue;
    private Set<Long> visitedWays;
    private Set<Long> visitedNodes;

    @Autowired
    public BFSConnectionPath(ElementRepo repo,
                             @Qualifier("DistanceFromOriginToMidHeuristic") Heuristic distanceHeuristic,
                             @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
                             @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                             @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                             @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());

        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();
    }

    @Override
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double availableDistance, double initialDistance, double targetDistance) {

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());

        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double currentRouteLength;
        double upperBound = availableDistance + initialDistance; // the remaining distance for the route

        queue.add(new PathTupleMain(null, originNode, originWay,
                0, 0, initialDistance, 0));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();
            double score;

            // the route has reached the target
            if (topTuple.getCurrentWay().getId() == targetWay.getId()) {
                double finalDistance = edgeDistanceCalculator
                        .calculateDistance(currentNode, targetNode, targetWay);
                double finalGradient = gradientCalculator.calculateGradient(currentNode, currentWay,
                        targetNode, targetWay,
                        finalDistance);

                // the route is at least as long as the one it replaces
                if (topTuple.getTotalLength() >= targetDistance) {
                    // create a new tuple representing the journey from the previous node to the final node
                    PathTuple returnTuple = new PathTupleMain(topTuple, targetNode,
                            targetWay, 0, finalDistance,
                            topTuple.getTotalLength() + finalDistance, finalGradient);
                    return returnTuple;
                }
            }

            // distance to origin point from the last explored way
            double lastDist = distanceFromOriginHeuristic.getScore(currentWay);

            // for each Way reachable from the current Way
            for (ConnectionPair pair : repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();
                score = 0;
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();

                // skip where this way or node has already been explored
                if (visitedWays.contains(selectedWay.getId()) ||
                        this.visitedNodes.contains(connectingNode.getId())) {
                    continue;
                }

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }

                double currentDistanceScore
                        = distanceFromOriginHeuristic.getScore(selectedWay);

                // if the current distance score is less than the previous Way's, that
                // is it is further away, then reduce the score
                if (currentDistanceScore < lastDist) {
                    score += DISTANCE_FROM_ORIGIN_BONUS;
                }

                if (distanceToNext < PREFERRED_MIN_LENGTH) {
                    score -= PREFERRED_MIN_LENGTH_PENALTY;
                }

                if (distanceToNext >= PREFERRED_LENGTH) {
                    score += PREFERRED_LENGTH_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                // call private method to add scores
                score += addScores(selectedWay, gradient, RANDOM_REDUCER);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext, gradient);
                queue.add(toAdd);

                visitedWays.add(currentWay.getId());
                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        return new PathTupleMain(null, null, null, -10000000,
                -1, -1, -1);
    }
}
