package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
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
public class BFSConnectionPath implements ILSGraphSearch {
    private ElementRepo repo; // the repository of Ways and Nodes
    private Heuristic distanceFromOriginHeursitic;
    private Heuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;
    private ElevationHeuristic elevationHeuristic;
    private double currentRouteLength;
    Set<Long> visitedWays;

    private double maxGradient = 0.8; // is user-defined
    private final double REPEATED_EDGE_PENALTY = 5; // deducted from score where
    // edge/Way has been previously visited
    private final double RANDOM_REDUCER = 5; // divides into random number added to the
    // score
    private final double MINIMUM_LENGTH = 100; // minimum length of way to avoid
    // subtracting a score penalty
    private final double MINIMUM_LENGTH_PENALTY = 0.5;
    private final long TIME_LIMIT = 1000;


    private PriorityQueue<PathTuple> queue;


    public BFSConnectionPath(ElementRepo repo, Heuristic distanceHeuristic,
                             Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator,
                             ElevationHeuristic elevationHeuristic, DistanceCalculator distanceCalculator) {
        this.repo = repo;
        this.distanceFromOriginHeursitic = distanceHeuristic;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;
        this.currentRouteLength = 0;
        this.visitedWays = new HashSet<>();
    }

    @Override
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double distance) {

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
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
                return topTuple;
            }

            // distance to origin point from the last explored way
            double lastDist = distanceFromOriginHeursitic.getScore(currentWay);

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
                        = distanceFromOriginHeursitic.getScore(selectedWay);

                // if the current distance score is less than the previous Way's, that
                // is it is further away, then skip this iteration
                if (currentDistanceScore < lastDist) {
                    score -= 5;
                }

                if (distanceToNext < MINIMUM_LENGTH) {
                    score -= MINIMUM_LENGTH_PENALTY;
                }

                score += addScores(selectedWay);

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

    private double addScores(Way selectedWay) {
        double score = 0;

        // drop the score where this way has already been explored
        if (visitedWays.contains(selectedWay.getId())) {
            score -= REPEATED_EDGE_PENALTY;
        }

        // add score reflecting correspondence of terrain features to user selectionss
        score += featuresHeuristic.getScore(selectedWay);

        // add a small random value to break ties
        score += (Math.random() / RANDOM_REDUCER);

        return score;
    }
}
