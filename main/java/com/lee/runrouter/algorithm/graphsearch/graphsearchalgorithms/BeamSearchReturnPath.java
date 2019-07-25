package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ConnectionPair;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
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
 * are kept in the list.
 */
@Component
@Qualifier("BeamSearchReturnPath")
public class BeamSearchReturnPath extends SearchAlgorithm implements GraphSearch {
    private final int BEAM_SIZE = 10000; // the max number of possible Nodes under review
    private final double REPEATED_EDGE_PENALTY = 1000; // deducted from score where
    // edge/Way has been previously visited
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 300; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 1;
    private final double PREFERRED_LENGTH = 1000     ;
    private final double PREFERRED_LENGTH_BONUS = 1;
    private final double DISTANCE_FROM_ORIGIN_BONUS = 0.75;
    private final long TIME_LIMIT = 1000;

    private List<PathTuple> queue;
    private Set<Long> visitedWays;
    private Set<Long> visitedNodes;

    private final double LOWER_SCALE = 0.75; // amount to scale upper lower bound on
    // run length by
    private final double UPPER_SCALE = 0.05; // amount to scale upper bound on
    // run length by

    @Autowired
    public BeamSearchReturnPath(ElementRepo repo,
                                @Qualifier("DistanceFromOriginToMidHeuristic") Heuristic distanceHeuristic,
                                @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
                                @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                                @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                                @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new ArrayList<>();
        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();
    }

    /**
     * Method for generating a route of the specified length,
     * connecting the initial position back to the origin point
     * of th route.
     * The method returns when it connects back to the origin
     * Way after exceeding the minimum required run length.
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
        this.visitedWays = new HashSet<>();
        this.visitedNodes = new HashSet<>();

        double currentRouteLength;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double upperBound = distance + (distance * UPPER_SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * LOWER_SCALE); // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, root.getNodeContainer().getNodes());

        queue.add(new PathTupleMain(null, originNode, root, 0, 0, 0));

        while (!queue.isEmpty()  && elapsedTime <= TIME_LIMIT) {
            queue.sort(Comparator
                    // sort by route segment score
                    .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());

            if (queue.size() > BEAM_SIZE) {
                queue = queue.subList(0, BEAM_SIZE);
            }

            PathTuple topTuple = queue.get(0);
            queue.remove(0);

            currentRouteLength = topTuple.getTotalLength();
            Way currentWay = topTuple.getCurrentWay();
            Node currentNode  = topTuple.getPreviousNode();
            double score;

            // return the first route to exceed the minimum length requirement.
            if (currentRouteLength > lowerBound) {
                // the route has returned to the origin
                if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {

                    double finalDistance = edgeDistanceCalculator
                            .calculateDistance(currentNode, repo.getOriginNode(), repo.getOriginWay());
                    // create a new tuple representing the journey from the previous node to the final node
                    PathTuple returnTuple = new PathTupleMain(topTuple, repo.getOriginNode(),
                            repo.getOriginWay(), 0, finalDistance, topTuple.getTotalLength() + finalDistance);
                    return returnTuple;
                }
            }

            // distance to origin point from the last explored way
            double lastDist = distanceFromOriginHeuristic.getScore(currentWay);

            // for each Way reachable from the current Way
            for (ConnectionPair pair: repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getTotalLength();
                score = 0;
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();


                // skip where this way has already been explored
                if (visitedWays.contains(selectedWay.getId())) {
                    continue;
                }

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }

                if (distanceToNext < PREFERRED_MIN_LENGTH) {
                    score -= PREFERRED_MIN_LENGTH_PENALTY;
                }

                if (distanceToNext >= PREFERRED_LENGTH) {
                    score += PREFERRED_LENGTH_BONUS;
                }

                double currentDistanceScore
                        = distanceFromOriginHeuristic.getScore(selectedWay);

                // if the current distance score is higher the previous Way's, that
                // is it is closer, increase the score
                if (currentDistanceScore > lastDist) {
                    score += DISTANCE_FROM_ORIGIN_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                score += super.addScores(selectedWay, gradient, RANDOM_REDUCER);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext);
                queue.add(toAdd);

                visitedWays.add(currentWay.getId());
                if (!repo.getOriginWay().getNodeContainer().getNodes().contains(visitedNodes)) {
                    this.visitedNodes.add(connectingNode.getId());
                }

                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        return new PathTupleMain(null, null, null, -1,
                -1, -1);
    }
}
