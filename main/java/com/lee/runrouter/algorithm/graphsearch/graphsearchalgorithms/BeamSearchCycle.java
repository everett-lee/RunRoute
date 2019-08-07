package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;


import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
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
    private final double PREFERRED_MIN_LENGTH = 150; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 0.005;
    private final double PREFERRED_LENGTH = 450; // minimum length to receive a score bonus
    private final double PREFERRED_LENGTH_BONUS = 0.005;
    private final double REPEATED_VISIT_DEDUCTION = 0.025; // score deduction for each repeat visit
    // to a Node or Way

    private final double LOWER_SCALE = 0.15; // amount to scale upper lower bound on
    // run length by
                private final double UPPER_SCALE = 0.15; // amount to scale upper bound on
    // run length by
    private final long TIME_LIMIT = 2000;

    private List<PathTuple> queue;
    private Map<Long, Integer> visitedNodesOutbound; // counts number of visits to each Node
    private Map<Long, Integer> visitedNodesInbound; // counts number of visits to each Node

    @Autowired
    public BeamSearchCycle(ElementRepo repo,
                           @Qualifier("DistanceFromOriginNodeHeuristicMain") DistanceFromOriginNodeHeursitic distanceFromOriginHeuristic,
                           @Qualifier("FeaturesHeuristicUsingDistance") FeaturesHeuristic featuresHeuristic,
                           @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                           @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                           @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceFromOriginHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new ArrayList<>();
        this.visitedNodesOutbound = new HashMap<>();
        this.visitedNodesInbound = new HashMap<>();
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
        this.visitedNodesOutbound = new HashMap<>();
        this.visitedNodesInbound = new HashMap<>();

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

                heuristicScore += super.addScores(selectedWay, distanceToNext, gradient);

                boolean overHalf = (currentRouteLength + distanceToNext) / targetDistance > 0.5;
                // call helper function to deduct scores for repeat visits to Node/Way
                heuristicScore += addRepeatedVisitScores(connectingNode, overHalf);

                double distanceScore = distanceFromOriginHeuristic.getScore(connectingNode, repo.getOriginNode(),
                        currentRouteLength, targetDistance);

                ScorePair segmentScore = new ScorePair(distanceScore, heuristicScore);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        segmentScore, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                queue.add(toAdd);

                // increase visited count for this Node if it is not in the
                // origin set
                if (!repo.getOriginWay().getNodeContainer().getNodes()
                        .contains(connectingNode.getId())) {
                    if (!overHalf) {
                        if (!this.visitedNodesOutbound.containsKey(connectingNode.getId())) {
                            this.visitedNodesOutbound.put(connectingNode.getId(), 1);
                        } else {
                            int current = this.visitedNodesOutbound.get(connectingNode.getId());
                            this.visitedNodesOutbound.put(connectingNode.getId(), current + 1);
                        }
                    } else {
                        if (!this.visitedNodesInbound.containsKey(connectingNode.getId())) {
                            this.visitedNodesInbound.put(connectingNode.getId(), 1);
                        } else {
                            int current = this.visitedNodesInbound.get(connectingNode.getId());
                            this.visitedNodesInbound.put(connectingNode.getId(), current + 1);
                        }
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

    private double addRepeatedVisitScores(Node connectingNode, boolean overHalf) {
        double score = 0;

        if (!overHalf) {
            if (this.visitedNodesOutbound.containsKey(connectingNode.getId())) {
                score -= this.visitedNodesOutbound.get(connectingNode.getId()) * REPEATED_VISIT_DEDUCTION;
            }
        } else {
            if (this.visitedNodesInbound.containsKey(connectingNode.getId())) {
                score -= this.visitedNodesInbound.get(connectingNode.getId()) * REPEATED_VISIT_DEDUCTION;
            }
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
