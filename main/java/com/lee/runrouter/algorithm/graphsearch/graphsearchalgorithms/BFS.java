package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;


import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.*;
import com.lee.runrouter.graph.elementrepo.*;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A greedy Best-First Search algorithm that utilises a priority queue
 * to explore the neighbouring node with the highest score (as assessed
 * by the heuristics) at each stage.
 */
@Component
@Qualifier("BFS")
public class BFS extends SearchAlgorithm implements GraphSearch {
    private final double REPEATED_EDGE_PENALTY = 1.5; // deducted from score where
    // edge/Way has been previously visited
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 25; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 1;
    private final double PREFERRED_LENGTH = 100; // preferred minimum travel distance between
    // nodes
    private final double PREFERRED_LENGTH_BONUS = 1; // penalty if distance is below
    // preferred
    private final double SCALE = 0.15; // amount to scale upper and lower bound on
    // run length by

    private PriorityQueue<PathTuple> queue;
    private Set<Long> visitedWays;

    @Autowired
    public BFS(ElementRepo repo,
               @Qualifier("DistanceFromOriginToMidHeuristic") Heuristic distanceHeuristic,
               @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
               @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
               @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
               @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
        visitedWays = new HashSet<>();
    }


    /**
     * Method for generating a route of the specified length,
     * that selects a path based on the given preferences.
     * The method returns as soon as a valid route of the minimum
     * length has been generated
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

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
        Set<Long> visitedWays = new HashSet<>();

        double currentRouteLength;
        double upperBound = distance + (distance * SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * SCALE); // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root, 0, 0, 0));


        // update the repository origin node
        repo.setOriginNode(originNode);

        while (!queue.isEmpty()) {
            PathTuple topTuple = queue.poll();
            Way currentWay = topTuple.getCurrentWay();
            Node currentNode  = topTuple.getPreviousNode();
            double score;

            currentRouteLength = topTuple.getTotalLength();

            // return the first route to exceed the minimum length requirement.
            if (currentRouteLength > lowerBound) {
                return topTuple;
            }

            // for each Way reachable from the the current Way
            for (ConnectionPair pair: repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();
                score = 0;
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();

                // skip where this way has already been explored
                if (visitedWays.contains(selectedWay.getId())) {
                    continue;
                }

                // calculates distance from the current Node to the Node connecting
                // the current Way to the selected Way
                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where maximum length exceeded
                }

                if (distanceToNext < PREFERRED_MIN_LENGTH) {
                    score -= PREFERRED_MIN_LENGTH_PENALTY;
                }

                if (distanceToNext >= PREFERRED_LENGTH) {
                    score += PREFERRED_LENGTH_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                score += super.addScores(selectedWay, gradient, RANDOM_REDUCER);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext);
                queue.add(toAdd);

                visitedWays.add(currentWay.getId());
            }
        }

        // null object returned in the event of an error
        return new PathTupleMain(null, null, null, Double.MIN_VALUE,
                -1, -1);
    }
}
