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
import java.util.stream.Collectors;

/**
 * A greedy Search algorithm that utilises a restricted List.
 * Vertices with the highest associated edge are added
 * to a priority queue, then selected and processed in order
 * of their score.
 */
@Component
@Qualifier("BFS")
public class BFS extends SearchAlgorithm implements GraphSearch {
    private final double LOWER_SCALE = 0.95; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 1.05; // amount to scale upper bound on
    // run length by
    private final double REPEATED_WAY_VISIT_PENALTY = 2;// a penalty applied for
    // revisiting a way traversed at an earlier stage of the route

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

        // tuples in the queue are sorted in descending order using
        // their score
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

        this.visitedNodesOutbound = new HashSet<>();
        this.visitedNodesInbound = new HashSet<>();
        this.visitedWays = new HashSet<>();
    }

    /**
     * Method for generating a route of the specified length
     * based on the given preferences.
     * This is achieved by conducting a greedy best first selection of Nodes
     * to form the required route. The method returns as soon as a valid
     * route of the minimum required length has been generated
     *
     * @param startingWay           the Way at which the run begins
     * @param coords         the coordinates at which the run begins
     * @param targetDistance the required target distance for the run
     * @return the head of a linked list of PathTuples representing
     * the route
     */
    @Override
    public PathTuple searchGraph(Way startingWay, double[] coords, double targetDistance) {
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getSegmentScore().getSum()).reversed());

        this.visitedNodesOutbound = new HashSet<>();
        this.visitedNodesInbound = new HashSet<>();
        this.visitedWays = new HashSet<>();

        long startTime = System.currentTimeMillis(); // the algorithm is time-limited
        // to break where a cycle is not found in the required time
        long elapsedTime = 0L;

        double upperBound = targetDistance * UPPER_SCALE; // upper bound of
        // run length
        double lowerBound = targetDistance * LOWER_SCALE; // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);

        // find the Node contained in the starting Way that is
        // closest to the the starting coordinates
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());

        queue.add(new PathTupleMain(null, originNode, startingWay,
                new ScorePair(0, 0), 0, 0, 0));

        // update the repository origin node
        repo.setOriginNode(originNode);

        // a Stream of each id corresponding to the Ways connected to the
        // starting Way. These are not added to the list of visited Ways
        List<Long> waysConnectedToStartIds = repo.getNodeToWay()
                .get(repo.getOriginNode().getId())
                .stream()
                .map(x -> x.getId())
                .collect(Collectors.toList());

        while (!queue.isEmpty() && elapsedTime <= timeLimit) {
            PathTuple topTuple = queue.poll();

            Node currentNode = topTuple.getCurrentNode();
            Way currentWay = topTuple.getCurrentWay();
            double currentRouteLength = topTuple.getTotalLength();

            // the route has returned to the origin
            if (currentWay.getId() == repo.getOriginWay().getId()) {
                PathTuple result = returnValidRoute(topTuple, currentNode,
                        currentWay, lowerBound, upperBound);
                // return the result if a valid route has been formed
                if (result != null) {
                    return result;
                }
            }

            // is the distance travelled is over half of the target
            boolean overHalf = currentRouteLength / targetDistance > 0.5;
            addToClosedList(currentNode, overHalf);

            // for each Way reachable from the the current Way
            for (ConnectionPair pair : this.repo.getConnectedWays(currentWay)) {
                double heuristicScore = 0;
                Node connectingNode = pair.getConnectingNode(); // the Node connecting
                // the intersecting Ways
                Way selectedWay = pair.getConnectingWay();

                // prune this branch if Node has already been processed
                if (nodeInClosedList(connectingNode, overHalf)) {
                    continue;
                }

                // calculates distance between the current Node to the Node connecting
                // the current Way to the selected Way
                double distanceToNext = this.edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                // checks to see if branch violates unlit or distance requirements
                // and prunes if it does
                if (super.pruneBranch(selectedWay,
                        currentRouteLength + distanceToNext,
                                    upperBound)) {
                    continue;
                }

                if (visitedWays.contains(selectedWay.getId())) {
                    heuristicScore -= REPEATED_WAY_VISIT_PENALTY;
                }

                // adds a bonus score where the distance between current Node
                // and the next is above the minimum threshold
                heuristicScore += super.applyDistanceScore(distanceToNext);

                double gradient = this.gradientCalculator
                        .calculateGradient(currentNode, currentWay, connectingNode,
                                selectedWay, distanceToNext);
                // prune this branch if the gradient is too step
                if (gradient > super.getMaxGradient()) {
                    continue;
                }

                heuristicScore += super.addScores(selectedWay, distanceToNext, gradient);

                overHalf = (currentRouteLength + distanceToNext) / targetDistance > 0.5;

                // provides a score bonus at the later stages of the route where
                // the connecting Node leads back to the starting point
                double distanceScore = distanceFromOriginHeuristic
                        .getScore(currentNode, connectingNode, originNode, currentRouteLength,
                                    targetDistance);

                ScorePair segmentScore = new ScorePair(distanceScore, heuristicScore);

                // create a new tuple representing this segment of the route
                // and add to the linked list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        segmentScore, distanceToNext, currentRouteLength + distanceToNext,
                        gradient);
                this.queue.add(toAdd);

                boolean connectedToStart = waysConnectedToStartIds
                        .contains(selectedWay.getId());
                if (!connectedToStart) {
                    visitedWays.add(selectedWay.getId());
                }
            }
            elapsedTime = (new Date()).getTime() - startTime;
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
     * Checks to see if the current route is eligible and returns it if
     * it is.
     * @param topTuple Tuple representing the last section of this path
     * @param currentNode the last visited Node
     * @param currentWay the last visited Way
     * @return a PathTuple containing the path segment linking the origin
     * and target ways
     */
    private PathTuple returnValidRoute(PathTuple topTuple, Node currentNode,
                                       Way currentWay, double lowerBound, double upperBound) {

        double currentRouteLength = topTuple.getTotalLength();
        double finalDistance = this.edgeDistanceCalculator
                .calculateDistance(currentNode, repo.getOriginNode(),
                        this.repo.getOriginWay());

        double totalRouteLength = currentRouteLength + finalDistance;
        // return the first valid cycle to exceed the min and max length requirement.
        if (totalRouteLength > lowerBound &&
                totalRouteLength < upperBound) {

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
        return  null; // return null if route length not within the required bounds
    }


    private boolean nodeInClosedList(Node connectingNode, boolean overHalf) {
        if (!overHalf) {
            return visitedNodesOutbound.contains(connectingNode.getId());
        } else {
            return  visitedNodesInbound.contains(connectingNode.getId());
        }
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