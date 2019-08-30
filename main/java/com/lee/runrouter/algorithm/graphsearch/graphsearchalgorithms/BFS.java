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
 * A greedy Search algorithm that utilises a priority queue.
 * Neighbouring nodes are selected and explored in order
 * of score(as assessed by the heuristics).
 */
@Component
@Qualifier("BFS")
public class BFS extends SearchAlgorithm implements GraphSearch {
    private final double MINIMUM_SCORING_DISTANCE = 500; // the minimum travelled
    // along a Way before the distance bonus is applied
    private final double DISTANCE_BONUS = 0.005;

    private final double LOWER_SCALE = 0.90; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 1.05; // amount to scale upper bound on
    // run length by
    private final double REPEATED_WAY_VISIT_PENALTY = 0.5; // a penalty applied for
    // revisiting a way traversed at an early stage of the route

    private PriorityQueue<PathTuple> queue;
    private Set<Long> visitedNodesOutbound; // Nodes visited in the outbound leg of this search
    private Set<Long> visitedNodesInbound; // Nodes visited in the inbound leg of this search
    private Set<Long> visitedWays; // Ways visited in the course of the entire search
    private long timeLimit = 500;

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
     * that selects a path informed by the given preferences.
     * This is achieved by conducting a greedy best first selection of ways
     * to form the required route. The method returns as soon as a valid
     * route of the minimum required length has been generated
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
        // sort visited Nodes by score in descending order
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

            // return the first valid cycle to exceed the minimum length requirement.
            if (currentRouteLength > lowerBound) {
                PathTuple result = returnValidPath(topTuple, currentNode, currentWay);
                if (result != null) {
                    return result;
                }
            }

            // has the route reached the halfway point
            boolean overHalf = (currentRouteLength) / targetDistance > 0.5;
            addToClosedList(currentNode, overHalf);

            // for each Way reachable from the the current Way
                for (ConnectionPair pair : this.repo.getConnectedWays(currentWay)) {

                    currentRouteLength = topTuple.getTotalLength();
                    heuristicScore = 0;
                    currentNode = topTuple.getCurrentNode(); // the last explored Node
                    Node connectingNode = pair.getConnectingNode(); // the Node connecting
                    // the intersecting Ways
                Way selectedWay = pair.getConnectingWay(); // the Way
                // connecting these two nodes

                // skip if this Node has already been explored
                if (nodeInClosedList(connectingNode, overHalf)) {
                    continue;
                }

                // skip if street lighting required and none available
                if (super.getAvoidUnlit()) {
                    if (!selectedWay.isLit()) {
                        continue;
                    }
                }

                // calculates targetDistance from the current Node to the Node connecting
                // the current Way to the selected Way
                double distanceToNext = this.edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                // skip where maximum length exceeded
                if (currentRouteLength + distanceToNext > upperBound) {
                    continue;
                }

                // deduct a penalty where this Way has already been
                // traversed
                if (visitedWays.contains(selectedWay.getId())
                        && distanceToNext > 0) {
                    heuristicScore -= REPEATED_WAY_VISIT_PENALTY;
                }

                // add a bonus where the distance travelled exceeds the
                // minimum required length
                if (distanceToNext > MINIMUM_SCORING_DISTANCE) {
                    heuristicScore += distanceToNext * DISTANCE_BONUS;
                }

                double gradient = this.gradientCalculator
                        .calculateGradient(currentNode, currentWay, connectingNode,
                                selectedWay, distanceToNext);

                // skip where the gradient of this section of the route
                // exceeds the maximum allowed
                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                heuristicScore += super.addScores(selectedWay, distanceToNext, gradient);

                // is the distance travelled is over half of the target?
                overHalf = (currentRouteLength + distanceToNext) / targetDistance > 0.5;

                double distanceScore = this.distanceFromOriginHeuristic
                        .getScore(connectingNode, repo.getOriginNode(),
                                currentRouteLength, targetDistance);

                ScorePair segmentScore = new ScorePair(distanceScore, heuristicScore);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        segmentScore, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                this.queue.add(toAdd);

                elapsedTime = (new Date()).getTime() - startTime;

                // add the current Way to the set of visited
                visitedWays.add(selectedWay.getId());
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


    private boolean nodeInClosedList(Node connectingNode, boolean overHalf) {
        // if the route length is over over half of the target
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
        // if the route length is over over half of the target
        if (!overHalf) {
            visitedNodesOutbound.add(previousNode.getId());
        } else {
            visitedNodesInbound.add(previousNode.getId());
        }
    }


}
