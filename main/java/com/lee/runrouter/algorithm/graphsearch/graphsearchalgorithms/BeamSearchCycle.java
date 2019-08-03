package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;


import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
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
@Qualifier("BeamSearchCycle")
public class BeamSearchCycle extends SearchAlgorithm implements GraphSearch {
    private final int BEAM_SIZE = 10000; // the max number of possible Nodes under review
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 300; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 0.25;
    private final double PREFERRED_LENGTH = 900;
    private final double PREFERRED_LENGTH_BONUS = 0.25;

    private final double LOWER_SCALE = 0.20; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 0.20; // amount to scale upper bound on
    // run length by

    private List<PathTuple> queue;
    private Hashtable<Long, Integer> visitedWays;
    private Hashtable<Long, Integer> visitedNodes;

    @Autowired
    public BeamSearchCycle(ElementRepo repo,
                      @Qualifier("DistanceFromOriginToMidHeuristic") Heuristic distanceHeuristic,
                      @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
                      @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                      @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                      @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new ArrayList<>();
        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();
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
        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();

        double currentRouteLength;
        double upperBound = distance + (distance * UPPER_SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * LOWER_SCALE); // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root,
                0, 0, 0, 0));

        // update the repository origin node
        repo.setOriginNode(originNode);

        while (!queue.isEmpty()) {
            queue.sort(Comparator
                    // sort by route segment score
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
                // the route has returned to the origin
                if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {

                    double finalDistance = edgeDistanceCalculator
                            .calculateDistance(currentNode, repo.getOriginNode(), repo.getOriginWay());
                    double finalGradient = gradientCalculator.calculateGradient(currentNode, currentWay,
                            repo.getOriginNode(), repo.getOriginWay(),
                            finalDistance);


                    // create a new tuple representing the journey from the previous node to the final node
                    PathTuple returnTuple = new PathTupleMain(topTuple, repo.getOriginNode(),
                            repo.getOriginWay(), 0, finalDistance,
                            topTuple.getTotalLength() + finalDistance, finalGradient);
                    return returnTuple;
                }
            }

            // for each Way reachable from the the current Way
            for (ConnectionPair pair: repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();
                score = 0;
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();

                if (super.getAvoidUnlit()) {
                    if (!selectedWay.isLit()) {
                        continue;
                    }
                }

                double distanceFromOriginNode =
                        HaversineCalculator.calculateDistanceII(connectingNode, repo.getOriginNode());



//                if (selectedWay.getId() == repo.getOriginWay().getId()) {
//                    score += 10000000;
//                }

                // skip where this way or node has already been explored
                if (this.visitedWays.containsKey(selectedWay.getId())) {
                    score -= this.visitedWays.get(selectedWay.getId()) * 0.1;
                }
                if (this.visitedNodes.containsKey(connectingNode.getId())) {
                    score -= this.visitedNodes.get(connectingNode.getId()) * 0.1;
                }

                // calculates distance from the current Node to the Node connecting
                // the current Way to the selected Way
                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where maximum length exceeded
                }

                if (currentRouteLength > distance * 0.50) {
                    score += 500 / distanceFromOriginNode;
                } else {
                   score += distanceFromOriginNode / 10000;
                }

                if (distanceToNext < PREFERRED_MIN_LENGTH) {
                    score -= PREFERRED_MIN_LENGTH_PENALTY;
                }

                if (distanceToNext >= PREFERRED_LENGTH) {
                    score += PREFERRED_LENGTH_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                score += super.addScores(selectedWay, gradient, RANDOM_REDUCER);
                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                queue.add(toAdd);

                if (!repo.getOriginWay().getNodeContainer().getNodes()
                        .contains(connectingNode.getId())) {
                    if (!this.visitedNodes.containsKey(connectingNode.getId())) {
                        this.visitedNodes.put(connectingNode.getId(), 1);
                    } else {
                        int current = this.visitedNodes.get(connectingNode.getId());
                        this.visitedNodes.put(connectingNode.getId(), current+1);
                    }
                }



                if (!this.visitedWays.containsKey(selectedWay.getId())) {
                    this.visitedWays.put(selectedWay.getId(), 1);
                } else {
                    int current = this.visitedWays.get(selectedWay.getId());
                    this.visitedWays.put(selectedWay.getId(), current+1);
                }
            }
        }

        // null object returned in the event of an error
        return new PathTupleMain(null, null, null, Double.MIN_VALUE,
                -1, -1, -1);
    }

}
