package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;


import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
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
 * Neighbouring nodes are explored and only those with the
 * highest score (as assessed by the heuristics) at each stage
 * are retained.
 */
@Component
@Qualifier("BeamSearchCycle")
public class BeamSearchCycle extends SearchAlgorithm implements GraphSearch {
    private final int BEAM_SIZE = 10000; // the max number of possible Nodes under review
    private final double RANDOM_REDUCER = 5000; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 300; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 0.0;
    private final double PREFERRED_LENGTH = 800; // minimum length to receive a score bonus
    private final double PREFERRED_LENGTH_BONUS = 0.25;
    private final double REPEATED_VISIT_DEDUCTION = 0.015; // score deduction for each repeat visit
    // to a Node or Way

    private final double LOWER_SCALE = 0.15; // amount to scale upper lower bound on
    // run length by
                private final double UPPER_SCALE = 0.15; // amount to scale upper bound on
    // run length by
    private final long TIME_LIMIT = 2000;

    private List<PathTuple> queue;
    private Hashtable<Long, Integer> visitedWays; // counts number of visits to each Way
    private Hashtable<Long, Integer> visitedNodes; // counts number of visits to each Node

    @Autowired
    public BeamSearchCycle(ElementRepo repo,
                           @Qualifier("DistanceFromOriginNodeHeuristicMain") DistanceFromOriginNodeHeursitic distanceFromOriginHeuristic,
                           @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
                           @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                           @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                           @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceFromOriginHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
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
     * @param root           the Way at which the run begins
     * @param coords         the coordinates at which the run begins
     * @param targetDistance the required targetDistance for the run
     * @return a PathTuple containing links to previous PathTuples,
     * the final Node and Way, their score, and the total length
     * of the path
     */
    @Override
    public PathTuple searchGraph(Way root, double[] coords, double targetDistance) {
        this.queue = new ArrayList<>();
        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();

        double currentRouteLength;
        long startTime = System.currentTimeMillis(); // the algorithm is time-limited
        // to break where a cycle is not found in the required time
        long elapsedTime = 0L;

        double upperBound = targetDistance + (targetDistance * UPPER_SCALE); // upper bound of
        // run length
        double lowerBound = targetDistance - (targetDistance * LOWER_SCALE); // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root,
                new ScorePair(0, 0), 0, 0, 0));

        // update the repository origin node
        repo.setOriginNode(originNode);

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            queue.sort(Comparator
                    // sort by route segment score
                    .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

            if (queue.size() > BEAM_SIZE) {
                queue = queue.subList(0, BEAM_SIZE);
            }

            // get and pop the first element in the queue
            PathTuple topTuple = queue.get(0);
            queue.remove(0);

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();
            currentRouteLength = topTuple.getTotalLength();
            double heuristicScore;

            // return the first route to exceed the minimum length requirement.
            if (currentRouteLength > lowerBound) {
                PathTuple result = returnValidPath(topTuple, currentNode, currentWay);
                if (result != null) {
                    return  result;
                }
            }

            // for each Way reachable from the the current Way
            for (ConnectionPair pair: repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();
                heuristicScore = 0;
                currentNode = topTuple.getPreviousNode(); // the last explored Node
                Node connectingNode = pair.getConnectingNode(); // the Node connecting
                // the intersecting Ways
                Way selectedWay = pair.getConnectingWay();

                // skip the way where street lighting required and none available
                if (super.getAvoidUnlit()) {
                    if (!selectedWay.isLit()) {
                        continue;
                    }
                }

                // call helper function to deduct scores for repeat visits to Node/Way
                heuristicScore += addRepeatedVisitScores(selectedWay, connectingNode);

                // calculates targetDistance from the current Node to the Node connecting
                // the current Way to the selected Way
                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where maximum length exceeded
                }

                heuristicScore += addLengthScores(distanceToNext);

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                heuristicScore += super.addScores(selectedWay, gradient, RANDOM_REDUCER);
                double distanceScore = distanceFromOriginHeursitic.getScore(connectingNode, repo.getOriginNode(),
                        currentRouteLength, targetDistance);

                ScorePair segmentScore = new ScorePair(distanceScore, heuristicScore);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        segmentScore, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                queue.add(toAdd);

                // increase visited count for this Way if it is not the
                // origin
                if (!this.visitedWays.containsKey(selectedWay.getId())) {
                    this.visitedWays.put(selectedWay.getId(), 1);
                } else {
                    int current = this.visitedWays.get(selectedWay.getId());
                    this.visitedWays.put(selectedWay.getId(), current + 1);
                }


                // increase visited count for this Node if it is not in the
                // origin set
                if (!repo.getOriginWay().getNodeContainer().getNodes()
                        .contains(connectingNode.getId())) {
                    if (!this.visitedNodes.containsKey(connectingNode.getId())) {
                        this.visitedNodes.put(connectingNode.getId(), 1);
                    } else {
                        int current = this.visitedNodes.get(connectingNode.getId());
                        this.visitedNodes.put(connectingNode.getId(), current + 1);
                    }
                }

                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        // null object returned in the event of an error
        return new PathTupleMain(null, null, null,
                new ScorePair(-1, -1), -1,
                -1, -1);
    }

    private PathTuple returnValidPath(PathTuple topTuple, Node currentNode, Way currentWay) {
        // the route has returned to the origin
        if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {

            double finalDistance = edgeDistanceCalculator
                    .calculateDistance(currentNode, repo.getOriginNode(), repo.getOriginWay());
            double finalGradient = gradientCalculator.calculateGradient(currentNode, currentWay,
                    repo.getOriginNode(), repo.getOriginWay(),
                    finalDistance);

            ScorePair finalScore = new ScorePair(0, 0);

            // create a new tuple representing the journey from the previous node to the final node
            PathTuple returnTuple = new PathTupleMain(topTuple, repo.getOriginNode(),
                    repo.getOriginWay(), finalScore, finalDistance,
                    topTuple.getTotalLength() + finalDistance, finalGradient);
            return returnTuple;
        }
        return null;
    }

    private double addRepeatedVisitScores(Way selectedWay, Node connectingNode) {
        double score = 0;

//        if (this.visitedWays.containsKey(selectedWay.getId())) {
//            score -= this.visitedWays.get(selectedWay.getId()) * REPEATED_VISIT_DEDUCTION;
//        }

        if (this.visitedNodes.containsKey(connectingNode.getId())) {
            score -= this.visitedNodes.get(connectingNode.getId()) * REPEATED_VISIT_DEDUCTION;
        }
        return score;
    }

    private double addLengthScores(double distanceToNext) {
        double score = 0;

        if (distanceToNext < PREFERRED_MIN_LENGTH) {
            score -= PREFERRED_MIN_LENGTH_PENALTY;
        }

        if (distanceToNext >= PREFERRED_LENGTH) {
            score += PREFERRED_LENGTH_BONUS;
        }

        return score;
    }


}
