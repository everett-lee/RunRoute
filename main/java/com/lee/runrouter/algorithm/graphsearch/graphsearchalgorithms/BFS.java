package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.EuclideanCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceTwo;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.pathnode.*;
import com.lee.runrouter.graph.elementrepo.*;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A greedy Search algorithm that utilises a restricted List.
 * Neighbouring nodes are explored and only those with the
 * highest score (as assessed by the heuristics) at each stage
 * are retained.
 */
@Component
@Qualifier("BFS")
public class BFS extends SearchAlgorithm implements GraphSearch {
    private final double MINIMUM_SCORING_DISTANCE = 500; // the minimum travelled
    // along a Way before the distance bonus is applied
    private final double DISTANCE_BONUS = 0.0005;

    private final double LOWER_SCALE = 0.95; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 1.05; // amount to scale upper bound on
    // run length by
    private final double REPEATED_WAY_VISIT_PENALTY = 1;// a penalty applied for
    // revisiting a way traversed at an early stage of the route

    private PriorityQueue<PathTuple> queue;
    private Set<Long> visitedNodesOutbound; // Nodes visited in the outbound leg of this search
    private Set<Long> visitedNodesInbound; // Nodes visited in the inbound leg of this search
    private HashSet<Long> visitedWays; // Ways visited in the course of the entire search
    private long timeLimit = 650;

    @Autowired
    public BFS(ElementRepo repo,
               @Qualifier("DistanceFromOriginNodeHeuristicMain") DistanceFromOriginNodeHeursitic distanceFromOriginHeuristic,
               @Qualifier("FeaturesHeuristicUsingDistance") FeaturesHeuristic featuresHeuristic,
               @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
               @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
               @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceFromOriginHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

        this.visitedNodesOutbound = new HashSet<>();
        this.visitedNodesInbound = new HashSet<>();
        this.visitedWays = new HashSet<>();
    }

    /**
     * Method for generating a route of the specified length,
     * that selects a path based on the given preferences.
     * This is achieved by conducting a greedy best first selection of ways
     * ot form the required route. The method returns as soon as a valid
     * route of the minimuh required length has been generated
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

        this.visitedNodesOutbound = new HashSet<>();
        this.visitedNodesInbound = new HashSet<>();
        this.visitedWays = new HashSet<>();

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
            Node currentNode = topTuple.getCurrentNode();
            currentRouteLength = topTuple.getTotalLength();
            double heuristicScore;

            // the route has returned to the origin
            if (currentWay.getId() == repo.getOriginWay().getId()) {
                double finalDistance = this.edgeDistanceCalculator
                        .calculateDistance(currentNode, repo.getOriginNode(),
                                this.repo.getOriginWay());

                double totalRouteLength = currentRouteLength + finalDistance;
                // return the first valid cycle to exceed the min and max length requirement.
                if (totalRouteLength > lowerBound &&
                        totalRouteLength < upperBound) {
                    PathTuple result = returnValidPath(topTuple, currentNode,
                            currentWay, finalDistance);
                    if (result != null) {
                        return result;
                    }
                }
            }



            boolean overHalf = (currentRouteLength) / targetDistance > 0.5;

            addToClosedList(currentNode, overHalf);


            // for each Way reachable from the the current Way
            for (ConnectionPair pair : this.repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();
                heuristicScore = 0;
                currentNode = topTuple.getCurrentNode(); // the last explored Node
                Node connectingNode = pair.getConnectingNode(); // the Node connecting
                // the intersecting Ways
                Way selectedWay = pair.getConnectingWay();

                // skip if this Node has already been explored
                if (nodeInClosedList(connectingNode, overHalf)) {
                    continue;
                }

                // calculates targetDistance from the current Node to the Node connecting
                // the current Way to the selected Way
                double distanceToNext = this.edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                // skip the way where street lighting required and none available
                if (super.getAvoidUnlit()) {
                    if (!selectedWay.isLit()) {
                        continue;
                    }
                }

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where maximum length exceeded
                }

                if (visitedWays.contains(selectedWay.getId())) {
                    heuristicScore -= REPEATED_WAY_VISIT_PENALTY;
                }

                if (distanceToNext > MINIMUM_SCORING_DISTANCE) {
                    double scoreLength = Math.max(distanceToNext, 750);
                    heuristicScore += scoreLength * DISTANCE_BONUS;
                }

                double gradient = this.gradientCalculator
                        .calculateGradient(currentNode, currentWay, connectingNode,
                                selectedWay, distanceToNext);

                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                heuristicScore += super.addScores(selectedWay, distanceToNext, gradient);

                // whether the distance travelled is over half of the target
                overHalf = (currentRouteLength + distanceToNext) / targetDistance > 0.5;

                double distanceScore = distanceFromOriginHeuristic
                        .getScore(currentNode, connectingNode, originNode, currentRouteLength,
                                    targetDistance);

                ScorePair segmentScore = new ScorePair(distanceScore, heuristicScore);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        segmentScore, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                this.queue.add(toAdd);

                boolean connectedToStart = repo.getNodeToWay().get(repo.getOriginNode().getId())
                        .stream()
                        .map(x -> x.getId())
                        .anyMatch(x -> x == (selectedWay.getId()));

                if (!connectedToStart) {
                    visitedWays.add(selectedWay.getId());
                }

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

    /***
     * Checks to see if the current path is eligible and returns it if
     * it is.
     * @param topTuple Tuple representing the last section of this path
     * @param currentNode the last visited Node
     * @param currentWay the last visited Way
     * @return a PathTuple containing the path segment linking the origin
     * and target ways
     */
    private PathTuple returnValidPath(PathTuple topTuple, Node currentNode,
                                      Way currentWay, double finalDistance) {
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


    private boolean nodeInClosedList(Node connectingNode, boolean overHalf) {
        if (!overHalf) {
            if (visitedNodesOutbound.contains(connectingNode.getId())) {
                return true;
            }
        } else {
            if (visitedNodesInbound.contains(connectingNode.getId())) {
                return true;
            }
        }
        return false;
    }

    private void addToClosedList(Node previousNode, boolean overHalf) {
        // add this Node to set of visited
        if (!overHalf) { // if the route length is over half of the total
            visitedNodesOutbound.add(previousNode.getId());
        } else {
            visitedNodesInbound.add(previousNode.getId());
        }
    }


}