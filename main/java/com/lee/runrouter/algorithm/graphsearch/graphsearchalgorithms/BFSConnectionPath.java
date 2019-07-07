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

import java.util.*;

/**
 * Variant of the BFS algorithm that restricts the next selected Way
 * to those closer to the starting point than the previous. It is used
 * to complete the circuit and return to the route's starting position
 * following execution of the BFS.
 */
public class BFSConnectionPath extends SearchAlgorithm implements ILSGraphSearch {
    private final double REPEATED_EDGE_PENALTY = 1.5; // deducted from score where
    // edge/Way has been previously visited
    private final double DISTANCE_FROM_ORIGIN_BONUS = 0.75;
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 50; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 1;
    private final double PREFERRED_LENGTH = 100;
    private final double PREFERRED_LENGTH_BONUS = 1;
    private final long TIME_LIMIT = 1000;

    private double maxGradient = 2; // is used-defined
    private PriorityQueue<PathTuple> queue;

    public BFSConnectionPath(ElementRepo repo, Heuristic distanceHeuristic, Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator, GradientCalculator gradientCalculator, ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
    }


    @Override
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double distance) {

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double currentRouteLength;
        double upperBound = distance;

        queue.add(new PathTupleMain(null, originNode, originWay,
                0, 0, 0));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {

            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();
            double score;
            currentRouteLength = topTuple.getTotalLength();

            // the route has reached the target
            if (topTuple.getCurrentWay().getId() == targetWay.getId()) {
                double finalDistance = edgeDistanceCalculator
                        .calculateDistance(currentNode, targetNode, targetWay);
                // create a new tuple representing the journey from the previous node to the final node
                PathTuple returnTuple = new PathTupleMain(topTuple, targetNode,
                        targetWay, 0, finalDistance, topTuple.getTotalLength() + finalDistance);
                return returnTuple;
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

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }

                double currentDistanceScore
                        = distanceFromOriginHeuristic.getScore(selectedWay);

                // if the current distance score is less than the previous Way's, that
                // is it is further away, then reduce the score
                if (currentDistanceScore > lastDist) {
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

                // skip to next where the gradient of this way exceeds
                // the maximum
                if (gradient > this.maxGradient) {
                    continue; }

                // call private method to add scores
                score += addScores(selectedWay, gradient, REPEATED_EDGE_PENALTY, RANDOM_REDUCER);

                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext);
                queue.add(toAdd);
                visitedWays.add(currentWay.getId());
                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        return new PathTupleMain(null, null, null, -10000000,
                -1, -1);
    }
}
