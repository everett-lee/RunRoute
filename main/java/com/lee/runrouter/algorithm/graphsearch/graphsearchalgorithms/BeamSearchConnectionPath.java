package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.algorithm.pathnode.ScorePair;
import com.lee.runrouter.graph.elementrepo.ConnectionPair;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Variant of the BFS algorithm that restricts the next selected Way
 * to those closer to the starting point than the previous. It is used
 * to greedily search for high scoring Ways which connect the starting position
 * to the target Way.
 */
@Component
@Qualifier("BeamSearch  ConnectionPath")
public class BeamSearchConnectionPath extends SearchAlgorithm implements ILSGraphSearch {
    private final double MINIMUM_SCORING_DISTANCE = 650; // the minimum travelled
    // along a Way before the distance bonus is applied
    private final double DISTANCE_BONUS = 0.025;
    final double REPEATED_WAY_VISIT_PENALTY = 10; // deducted from heuristic score
    // for visits to Ways included in the main route
    final double MAX_DISTANCE_FROM_TARGET_MULTIPLIER = 1.5; // maximum increase in
    // distance to target compared to previous node in the path's position

    private PriorityQueue<PathTuple> queue;
    private HashSet<Long> visitedWays; // ways visited in the course of this search
    private HashSet<Long> visitedNodes; // nodes visited in the course of this search
    private HashSet<Long> includedWays; // ways included in the main path
    private double minimumPathPercentage = 0.8; // length of this path segment as
    // a percentage of a the removed path segment required to serve as a valid
    // replacement

    private final double TIME_LIMIT = 500;

    public BeamSearchConnectionPath(ElementRepo repo,
                             @Qualifier("DirectDistanceHeuristic") DistanceFromOriginNodeHeursitic distanceFromOriginHeursitic,
                             @Qualifier("FeaturesHeuristicMain") FeaturesHeuristic featuresHeuristic,
                             @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                             @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                             @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceFromOriginHeursitic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getHeuristicScore()).reversed());
        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();
    }

    @Override
    public PathTuple connectPath(PathTuple origin, PathTuple target,
                                 double availableDistance, double targetDistance) {

        queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getHeuristicScore()).reversed());

        visitedWays = new HashSet<>();
        visitedNodes = new HashSet<>();

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        Way targetWay = target.getCurrentWay();
        Node targetNode = target.getCurrentNode();
        double currentRouteLength;
        // the remaining distance for the route
        double upperBound = availableDistance + origin.getTotalLength();

        queue.add(new PathTupleMain(null, origin.getCurrentNode(), origin.getCurrentWay(),
                origin.getSegmentScore(), origin.getSegmentLength(),origin.getTotalLength(), origin.getSegmentGradient()));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getCurrentNode();

            // the route has reached the target
            if (topTuple.getCurrentWay().getId() == targetWay.getId()) {
                PathTuple result = returnValidPath(topTuple, currentNode, currentWay
                        , targetNode, targetWay, targetDistance);
                // return this path if it is valid
                if (result != null) {
                    return result;
                }
            }

            double currentDistanceFromTarget
                    = distanceFromOriginHeuristic.getScore(currentNode, targetNode,
                    0, 0);

            // for each Way reachable from the current Way
            for (ConnectionPair pair : repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();

                currentNode = topTuple.getCurrentNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();
                double heuristicScore = 0;

                // continue where already visited
                if (wayOrNodeInClosedList(selectedWay, connectingNode)) {
                    continue;
                }

                if (includedWays.contains(selectedWay.getId())) {
                    heuristicScore -= REPEATED_WAY_VISIT_PENALTY;
                }

                double distanceFromSelectedToTarget
                        = distanceFromOriginHeuristic.getScore(connectingNode, targetNode,
                        0, 0);

                if (distanceFromSelectedToTarget > currentDistanceFromTarget
                        * MAX_DISTANCE_FROM_TARGET_MULTIPLIER) {
                    continue;
                }

                // skip this Way if unlit when lighting is required
                if (super.getAvoidUnlit()) {
                    if (!selectedWay.isLit()) {
                        continue;
                    }
                }

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }


                // apply the bonus for longer Ways
                if (distanceToNext > MINIMUM_SCORING_DISTANCE) {
                    heuristicScore += distanceToNext * DISTANCE_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                // call private method to add heuristic scores
                heuristicScore += addScores(selectedWay, distanceToNext, gradient);

                ScorePair score = new ScorePair(0, heuristicScore);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext, gradient);
                queue.add(toAdd);

                addToClosedLists(selectedWay, connectingNode);

                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        return new PathTupleMain(null, null, null, new ScorePair(-1, -10000),
                -1, -1, -1);
    }

    /***
     * Checks to see if the current path is eligible and returns it if
     * it is.
     * @param topTuple Tuple representing the last section of this path
     * @param currentNode the last visited Node
     * @param currentWay the last visited Way
     * @param targetNode the target Node of this segment
     * @param targetWay the target Way of this segment
     * @param targetDistance the required distance for this path segment
     * @return a PathTuple containing the path segment linking the origin
     * and target ways
     */
    private PathTuple returnValidPath(PathTuple topTuple, Node currentNode, Way currentWay,
                                      Node targetNode, Way targetWay, double targetDistance) {
        ScorePair finalScore = topTuple.getSegmentScore();

        double finalDistance = edgeDistanceCalculator
                .calculateDistance(currentNode, targetNode, targetWay);
        double finalGradient = gradientCalculator.calculateGradient(currentNode, currentWay,
                targetNode, targetWay,
                finalDistance);

        if (topTuple.getTotalLength() >= targetDistance * minimumPathPercentage) {
            if (checkMinLength(topTuple)) {
                // create a new tuple representing the journey from the previous node to the final node
                PathTuple returnTuple = new PathTupleMain(topTuple, targetNode,
                        targetWay, finalScore, finalDistance,
                        topTuple.getTotalLength() + finalDistance, finalGradient);
                return returnTuple;
            }
        }
        return null;
    }


    // Updates the set of visited Ways and Nodes
    private void addToClosedLists(Way selectedWay, Node connectingNode) {
        if (!visitedWays.contains(selectedWay.getId())) {
            visitedWays.add(selectedWay.getId());
        }

        if (!visitedNodes.contains(connectingNode)) {
            visitedNodes.add(connectingNode.getId());
        }
    }

    private boolean wayOrNodeInClosedList(Way selectedWay, Node connectingNode) {
        return visitedWays.contains(selectedWay.getId()) ||
                visitedNodes.contains(connectingNode.getId());
    }

    @Override
    public void setIncludedWays(HashSet<Long> includedWays) {
        this.includedWays = includedWays;
    }

    @Override
    public void resetVisitedNodes() {
        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();
    }


    // check the length of the path segment contains
    // at least some minimum number of nodes to
    // avoid reducing the path size     significantly
    public boolean checkMinLength(PathTuple head) {
        int count = 0;
        final int MIN_LENGTH = 3;

        while (head != null && count <= MIN_LENGTH) {
            count++;
            head = head.getPredecessor();
        }

        return (count >= MIN_LENGTH);
    }
}
