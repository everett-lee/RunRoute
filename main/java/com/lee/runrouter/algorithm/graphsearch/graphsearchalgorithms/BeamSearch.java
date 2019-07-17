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
 * A greedy Search algorithm that utilises a restricted List.
 * Neghbouring nodes are explored and only those with the
 * highest score (as assessed by the heuristics) at each stage
 * are kept in the list.
 */
@Component
@Qualifier("BeamSearch")
public class BeamSearch extends SearchAlgorithm implements GraphSearch {
    private final int BEAM_SIZE = 10000; // the max number of possible Nodes under review
    private final double REPEATED_EDGE_PENALTY = 1.5; // deducted from score where
    // edge/Way has been previously visited
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 50; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 1;
    private final double PREFERRED_LENGTH = 100;
    private final double PREFERRED_LENGTH_BONUS = 1;
    private final double SCALE = 0.05; // amount to scale upper and lower bound on
    // run length by

    private List<PathTuple> queue;

    @Autowired
    public BeamSearch(ElementRepo repo,
                      @Qualifier("DistanceFromOriginToMidHeuristic") Heuristic distanceHeuristic,
                      @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
                      @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                      @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                      @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new ArrayList<>();
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
        this.queue = new ArrayList<>();

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
            queue.sort(Comparator
                    .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());

            if (queue.size() > BEAM_SIZE) {
                queue = queue.subList(0, BEAM_SIZE);
            }

            PathTuple topTuple = queue.get(0);
            queue.remove(0);

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

                score += super.addScores(selectedWay, gradient, REPEATED_EDGE_PENALTY, RANDOM_REDUCER);

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
