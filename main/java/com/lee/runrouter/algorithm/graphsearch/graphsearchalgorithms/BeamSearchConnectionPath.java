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
 * A variant of the BeamSearch Algorithm for finding paths
 * between two specified points. This algorithm is used to
 * fill gaps created by removing path edges as part of the iterated
 * local search metaheuristic.
 */
public class BeamSearchConnectionPath implements ILSGraphSearch {
    private ElementRepo repo; // the repository of Ways and Nodes
    private Heuristic distanceFromOriginHeursitic;
    private Heuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;
    private ElevationHeuristic elevationHeuristic;
    private double currentRouteLength;
    private double maxGradient = 0.8; // is user-defined

    private final int BEAM_SIZE = 15; // the max number of possible Nodes under review
    private final double REPEATED_EDGE_PENALTY = 1; // deducted from score where
    // edge/Way has been previously visited
    private final double DISTANCE_FROM_ORIGIN_PENALTY = 1;
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_LENGTH = 100; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_LENGTH_PENALTY = 0.5;
    private final long TIME_LIMIT = 1000;

    private final Set<Long> visitedWays;
    private List<PathTuple> queue;


    public BeamSearchConnectionPath(ElementRepo repo, Heuristic distanceHeuristic,
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

    /**
     * Method for generating a route of the specified length,
     * that selects a path based on the given preferences.
     * The method returns as soon as the target way is reached.
     *
     * @param originNode the starting node of of the connecting path
     * @param originWay the starting way of the connecting path
     * @param targetNode the target node of the connecting path
     * @param targetWay the target way of the connecting path
     * @param distance the total distance available to travel
     * @return a PathTuple that is the head of the linked list
     * of PathTuples containing hte path back to the origin point
     */
    @Override
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double distance) {
        this.queue = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double upperBound = distance;

        // add the starting PathTuple of the chain, with no link
        queue.add(new PathTupleMain(null, originNode, originWay,
                0, 0, 0));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            queue.sort(Comparator
                    .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());

            // reduce the size of the queue where it exceeds beam size
            if (queue.size() > BEAM_SIZE) {
                queue = queue.subList(0, BEAM_SIZE);
            }

            PathTuple topTuple = queue.get(0);
            queue.remove(0);

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
                // is it is further away, then reduce the score
                if (currentDistanceScore < lastDist) {
                    score -= DISTANCE_FROM_ORIGIN_PENALTY;
                }

                if (distanceToNext < PREFERRED_LENGTH) {
                    score -= PREFERRED_LENGTH_PENALTY;
                }

                // call private method to add scores
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
