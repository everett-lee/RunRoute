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
@Qualifier("BFSCycle")
public class BFSCycle extends SearchAlgorithm implements GraphSearch {
    private final double MINIMUM_SCORING_DISTANCE = 550;
    private final double DISTANCE_BONUS = 0.025;

    private final double LOWER_SCALE = 0.90; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 1.10 ; // amount to scale upper bound on
    // run length by

    private PriorityQueue<PathTuple> queue;
    private Set<Long> visitedWaysOutbound; // counts number of visits to each Node
    private Set<Long> visitedWaysInbound; // counts number of visits to each Node
    private long timeLimit = 1500;

    @Autowired
    public BFSCycle(ElementRepo repo,
                           @Qualifier("DistanceFromOriginNodeHeuristicMain") DistanceFromOriginNodeHeursitic distanceFromOriginHeuristic,
                           @Qualifier("FeaturesHeuristicUsingDistance") FeaturesHeuristic featuresHeuristic,
                           @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                           @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                           @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceFromOriginHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

        this.visitedWaysOutbound = new HashSet<>();
        this.visitedWaysInbound = new HashSet<>();
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
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

        this.visitedWaysOutbound = new HashSet<>();
        this.visitedWaysInbound = new HashSet<>();

        double currentRouteLength;
        long startTime = System.currentTimeMillis(); // the algorithm is time-limited
        // to break where a cycle is not found in the required time
        long elapsedTime = 0L;

        double upperBound = targetDistance * UPPER_SCALE; // upper bound of
        // run length
        double lowerBound = targetDistance * LOWER_SCALE; // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root,
                new ScorePair(0, 0), 0, 0, 0));

        // update the repository origin node
        repo.setOriginNode(originNode);

        while (!queue.isEmpty() && elapsedTime <= timeLimit) {
            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();
            currentRouteLength = topTuple.getTotalLength();
            double heuristicScore;

            // return the first valid cycle to exceed the minimum length requirement.
            if (currentRouteLength > lowerBound) {
                PathTuple result = returnValidPath(topTuple, currentNode, currentWay);
                if (result != null) {
                    return result;
                }
            }

            // for each Way reachable from the the current Way
            for (ConnectionPair pair: this.repo.getConnectedWays(currentWay)) {

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
                double distanceToNext = this.edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where maximum length exceeded
                }

                //heuristicScore += addLengthScores(distanceToNext);
                if (distanceToNext > MINIMUM_SCORING_DISTANCE) {
                    heuristicScore += distanceToNext * DISTANCE_BONUS;
                }

                double gradient = this.gradientCalculator
                        .calculateGradient(currentNode, currentWay, connectingNode,
                                selectedWay, distanceToNext);

                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                heuristicScore += super.addScores(selectedWay, distanceToNext, gradient);

                boolean overHalf = (currentRouteLength + distanceToNext) / targetDistance > 0.5;
                // call helper function to deduct scores for repeat visits to Node/Way

                if (!overHalf) {
                    if (visitedWaysOutbound.contains(selectedWay.getId())) {
                        continue;
                    }
                } else {
                    if (visitedWaysInbound.contains(selectedWay.getId())) {
                        continue;
                    } else if (visitedWaysOutbound.contains(selectedWay.getId())) {
                        heuristicScore -= 3;
                    }
                }

                double distanceScore = this.distanceFromOriginHeuristic
                        .getScore(connectingNode, repo.getOriginNode(),
                                currentRouteLength, targetDistance);

                ScorePair segmentScore = new ScorePair(distanceScore, heuristicScore);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        segmentScore, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                this.queue.add(toAdd);

                addVisitedWay(selectedWay, overHalf);

                elapsedTime = (new Date()).getTime() - startTime;

            }
        }

        // null object returned in the event of an error
        return new PathTupleMain(null, null, null,
                new ScorePair(-1, -10000), -1,
                -1, -1);
    }

    @Override
    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    private PathTuple returnValidPath(PathTuple topTuple, Node currentNode, Way currentWay) {
        // the route has returned to the origin
        if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {

            double finalDistance = this.edgeDistanceCalculator
                    .calculateDistance(currentNode, repo.getOriginNode(), this.repo.getOriginWay());
            double finalGradient = this.gradientCalculator.calculateGradient(currentNode, currentWay,
                    this.repo.getOriginNode(), this.repo.getOriginWay(),
                    finalDistance);

            ScorePair finalScore = new ScorePair(0, 0);

            // create a new tuple representing the journey from the previous node to the final node
            PathTuple returnTuple = new PathTupleMain(topTuple, this.repo.getOriginNode(),
                    this.repo.getOriginWay(), finalScore, finalDistance,
                    topTuple.getTotalLength() + finalDistance, finalGradient);
            return returnTuple;
        }
        return null;
    }



    private void addVisitedWay(Way selectedWay, boolean overHalf) {
        // increase visited count for this Way if it is not in the
        // origin set
        if (this.repo.getOriginWay().getId() != selectedWay.getId()) {
            if (!overHalf) {
                if (!visitedWaysOutbound.contains(selectedWay.getId())) {
                    visitedWaysOutbound.add(selectedWay.getId());
                }
            } else {
                if (!visitedWaysInbound.contains(selectedWay.getId())) {
                    visitedWaysInbound.add(selectedWay.getId());
                }
            }
        }
    }


}
