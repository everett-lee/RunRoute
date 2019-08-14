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
 * to complete the circuit and return to the route's starting position
 * following execution of the BFS.
 */
@Component
@Qualifier("BFSConnectionPath")
public class BFSConnectionPath extends SearchAlgorithm implements ILSGraphSearch {
    private final double MINIMUM_SCORING_DISTANCE = 650;
    private final double DISTANCE_BONUS = 0.025;
    final double REPEATED_WAY_VISIT_PENALTY = 10;
    final double MAX_DISTANCE_FROM_TARGET_MULTIPLIER = 1.5;

    private PriorityQueue<PathTuple> queue;
    private HashSet<Long> visitedWays; // ways visited in the course of this search
    private HashSet<Long> includedWays; // ways included in the main path
    private HashSet<Long> visitedNodes;
    private double minimumPathPercentage = 0.8;

    private final double TIME_LIMIT = 500;

    public BFSConnectionPath(ElementRepo repo,
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
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double availableDistance, double initialDistance, double targetDistance) {

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getHeuristicScore()).reversed());

        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double currentRouteLength;
        double upperBound = availableDistance + initialDistance; // the remaining distance for the route

        queue.add(new PathTupleMain(null, originNode, originWay,
                new ScorePair(0, 0), 0, initialDistance, 0));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();

            // the route has reached the target
            if (topTuple.getCurrentWay().getId() == targetWay.getId()) {
                PathTuple result = returnValidPath(topTuple, currentNode, currentWay
                        , targetNode, targetWay, targetDistance);

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

                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();
                double heuristicScore = 0;

                if (wayOrNodeInClosedList(selectedWay, connectingNode)) {
                    continue;
                }

                if (this.includedWays.contains(selectedWay.getId())) {
                    heuristicScore -= REPEATED_WAY_VISIT_PENALTY;
                }

                double distanceFromSelectedToTarget
                        = distanceFromOriginHeuristic.getScore(connectingNode, targetNode,
                        0, 0);

                if (distanceFromSelectedToTarget > currentDistanceFromTarget
                        * MAX_DISTANCE_FROM_TARGET_MULTIPLIER) {
                    continue;
                }

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

                if (distanceToNext > MINIMUM_SCORING_DISTANCE) {
                    heuristicScore += distanceToNext * DISTANCE_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                // call private method to add scores
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


    private void addToClosedLists(Way selectedWay, Node connectingNode) {
        // add this way to the visited list
        if (!this.visitedWays.contains(selectedWay.getId())) {
            this.visitedWays.add(selectedWay.getId());
        }

        if (!this.visitedNodes.contains(connectingNode)) {
            this.visitedNodes.add(connectingNode.getId());
        }
    }

    private boolean wayOrNodeInClosedList(Way selectedWay, Node connectingNode) {
        return this.visitedWays.contains(selectedWay.getId()) ||
                this.visitedNodes.contains(connectingNode.getId());
    }

    @Override
    public void setIncludedWays(HashSet<Long> includedWays) {
        this.includedWays = includedWays;
    }

    @Override
    public void resetVisitedWays() {
        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();
    }


    public boolean checkMinLength(PathTuple head) {
        int count = 0;

        while (head != null && count <= 3) {
            count++;
            head = head.getPredecessor();
        }

        return (count >= 3);
    }

    public void setMinimumPathPercentage(double minimumPathPercentage) {
        this.minimumPathPercentage = minimumPathPercentage;
    }
}
