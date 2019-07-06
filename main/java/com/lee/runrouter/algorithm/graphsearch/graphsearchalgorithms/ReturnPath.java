package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.AlgoHelpers;
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
public class ReturnPath extends SearchAlgorithm implements GraphSearch {
    private final int BEAM_SIZE = 25; // the max number of possible Nodes under review
    private final double REPEATED_EDGE_PENALTY = 1; // deducted from score where
    // edge/Way has been previously visited
    private final double RANDOM_REDUCER = 50; // divides into random number added to the
    // score
    private final double MINIMUM_LENGTH = 5; // minimum length of way to avoid
    // skipping
    private final double PREFERRED_LENGTH = 25; // preferred minimum travel distance between
    // nodes
    private final double PREFERRED_LENGTH_PENALTY = 0.5; // penalty if distance is below
    // preferred
    private final double DISTANCE_FROM_ORIGIN_PENALTY = 10;

    private PriorityQueue<PathTuple> queue;
    private double maxGradient = 2; // is used-defined

    private final double LOWER_SCALE = 0.7; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 0.05; // amount to scale upper bound on
    // run length by

    public ReturnPath(ElementRepo repo, Heuristic distanceHeuristic, Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator, GradientCalculator gradientCalculator, ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        // compare priority queue items by their assigned score in descending order
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
    }


    /**
     * Method for generating a route of the specified length,
     * connecting the initial position back to the origin point
     * of th route.
     * The method returns when it connects back to the origin
     * Way after exceeding the minimum required run length.
     *
     * @param root the Way at which the run begins
     * @param coords the coordinates at which the run begins
     * @param distance the required distance for the run
     * @return a PathTuple containing links to previous PathTuples,
     *  the final Node and Way, their score, and the total length
     *  of the path
     */
    @Override
    public PathTuple searchGraph(Way root, double[] coords, double distance) {
        Set<Long> visitedWays = new HashSet<>();

        double currentRouteLength;
        double upperBound = distance + (distance * UPPER_SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * LOWER_SCALE); // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, root.getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root,0, 0, 0));

        while (!queue.isEmpty()) {
            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode  = topTuple.getPreviousNode();
            double score;
            currentRouteLength = topTuple.getTotalLength();

            // return the first route to exceed the minimum length requirement.
            if (currentRouteLength > lowerBound) {
                // the route has returned to the origin
                if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {

                    double finalDistance = edgeDistanceCalculator
                            .calculateDistance(currentNode, repo.getOriginNode(), repo.getOriginWay());
                    // create a new tuple representing the journey from the previous node to the final node
                    PathTuple returnTuple = new PathTupleMain(topTuple, repo.getOriginNode(),
                            repo.getOriginWay(), 0, finalDistance, topTuple.getTotalLength() + finalDistance);
                    return returnTuple;
                }
            }

            // distance to origin point from the last explored way
            double lastDist = distanceFromOriginHeuristic.getScore(currentWay);

            // for each Way reachable from the current Way
            for (ConnectionPair pair: repo.getConnectedWays(currentWay)) {
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

                if (distanceToNext < MINIMUM_LENGTH) {
                    continue; // skip short connections. Used to cull shorter sections and force
                    // failure if necessary to move to next stage of algorithm.
                }

                if (distanceToNext < PREFERRED_LENGTH) {
                    score -= PREFERRED_LENGTH_PENALTY;
                }

                double currentDistanceScore
                        = distanceFromOriginHeuristic.getScore(selectedWay);

                // if the current distance score is less than the previous Way's, that
                // is it is further away, then skip this iteration
                if (currentDistanceScore < lastDist) {
                    score -= DISTANCE_FROM_ORIGIN_PENALTY;
                }

                visitedWays.add(currentWay.getId());

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                // skip to next where the gradient of this way exceeds
                // the maximum
                if (gradient > this.maxGradient) {
                    continue; }


                score += addScores(selectedWay, gradient, REPEATED_EDGE_PENALTY, RANDOM_REDUCER);

                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext);
                queue.add(toAdd);
            }
        }

        return new PathTupleMain(null, null, null, -1,
                -1, -1);
    }
}