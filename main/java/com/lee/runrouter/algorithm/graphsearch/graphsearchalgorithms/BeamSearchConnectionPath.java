package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
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
@Qualifier("BeamSearchConnectionPath")
public class BeamSearchConnectionPath extends SearchAlgorithm implements ILSGraphSearch {
    private final int BEAM_SIZE = 10000; // the max number of possible Nodes under review
    private final double PREFERRED_MIN_LENGTH = 50; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 0.005;
    private final double PREFERRED_LENGTH = 450; // minimum length to receive a score bonus
    private final double PREFERRED_LENGTH_BONUS = 0.005;
    private final double REPEATED_VISIT_DEDUCTION = 0.0075; // score deduction for each repeat visit
    // to a Node or Way

    private List<PathTuple> queue;
    private Hashtable<Long, Integer> visitedWays;
    private Hashtable<Long, Integer> visitedNodes;

    private final double TIME_LIMIT = 500;

    public BeamSearchConnectionPath(ElementRepo repo,
                             @Qualifier("DirectDistanceHeuristic") DistanceFromOriginNodeHeursitic distanceFromOriginHeursitic,
                             @Qualifier("FeaturesHeuristicMain") FeaturesHeuristic featuresHeuristic,
                             @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                             @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                             @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceFromOriginHeursitic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        this.queue = new ArrayList<>();
        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();
    }

    @Override
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double availableDistance, double initialDistance, double targetDistance) {

        this.queue = new ArrayList<>();

        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double currentRouteLength;
        double upperBound = availableDistance + initialDistance; // the remaining distance for the route

        // REPLACE WITH COPY OF START NODE  W/0 PRED
        queue.add(new PathTupleMain(null, originNode, originWay,
                new ScorePair(0, 0), 0, initialDistance, 0));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            queue.sort(Comparator
                    // sort by route segment score
                    .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

            if (queue.size() > BEAM_SIZE) {
                queue = queue.subList(0, BEAM_SIZE);
            }

            PathTuple topTuple = queue.get(0);
            queue.remove(0);


            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();
            ScorePair finalScore = topTuple.getSegmentScore();

            // the route has reached the target
            if (topTuple.getCurrentWay().getId() == targetWay.getId()) {
                double finalDistance = edgeDistanceCalculator
                        .calculateDistance(currentNode, targetNode, targetWay);
                double finalGradient = gradientCalculator.calculateGradient(currentNode, currentWay,
                        targetNode, targetWay,
                        finalDistance);

                // TODO: CHECK IF THIS SHOULD BE 1
                if (topTuple.getTotalLength() >= 1) {
                    if (checkMinLength(topTuple)) {
                        // create a new tuple representing the journey from the previous node to the final node
                        PathTuple returnTuple = new PathTupleMain(topTuple, targetNode,
                                targetWay, finalScore, finalDistance,
                                topTuple.getTotalLength() + finalDistance, finalGradient);
                        return returnTuple;
                    }
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

                double distanceFromSelectedToTarget
                        = distanceFromOriginHeuristic.getScore(connectingNode, targetNode,
                        0, 0);

                if (distanceFromSelectedToTarget > currentDistanceFromTarget * 1.5) {
                    continue;
                }

                heuristicScore += addRepeatedVisitScores(selectedWay, connectingNode);

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

                heuristicScore += addLengthScores(distanceToNext);

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

                if (!repo.getOriginWay().getNodeContainer().getNodes()
                        .contains(connectingNode.getId())) {
                    if (!this.visitedNodes.containsKey(connectingNode.getId())) {
                        this.visitedNodes.put(connectingNode.getId(), 1);
                    } else {
                        int current = this.visitedNodes.get(connectingNode.getId());
                        this.visitedNodes.put(connectingNode.getId(), current + 1);
                    }
                }

                if (!this.visitedWays.containsKey(selectedWay.getId())) {
                    this.visitedWays.put(selectedWay.getId(), 1);
                } else {
                    int current = this.visitedWays.get(selectedWay.getId());
                    this.visitedWays.put(selectedWay.getId(), current + 1);
                }

                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        return new PathTupleMain(null, null, null, new ScorePair(-1, -1),
                -1, -1, -1);
    }

    @Override
    public void resetVisitedNodes() {

    }

    private double addRepeatedVisitScores(Way selectedWay, Node connectingNode) {
        double score = 0;

        // skip where this way or node has already been explored
        if (this.visitedWays.containsKey(selectedWay.getId())) {
            score -= this.visitedWays.get(selectedWay.getId()) * REPEATED_VISIT_DEDUCTION;
        }
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

    public boolean checkMinLength(PathTuple head) {
        int count = 0;

        while (head != null && count <= 3) {
            count ++;
            head = head.getPredecessor();
        }

        return (count >= 3);
    }
}
