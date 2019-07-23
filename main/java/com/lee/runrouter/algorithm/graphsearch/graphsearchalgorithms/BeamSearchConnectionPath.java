package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

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
 * A variant of the BeamSearch Algorithm for finding paths
 * between two specified points. This algorithm is used to
 * fill gaps created by removing path edges as part of the iterated
 * local search metaheuristic.
 */
@Component
@Qualifier("BeamSearchConnectionpath")
public class BeamSearchConnectionPath extends SearchAlgorithm implements ILSGraphSearch {
    private final int BEAM_SIZE = 7500; // the max number of possible Nodes under review
    private final double DISTANCE_FROM_ORIGIN_BONUS = 0.75;
    private final double RANDOM_REDUCER = 500; // divides into random number added to the
    // score
    private final double PREFERRED_MIN_LENGTH = 50; // minimum length of way to avoid
    // subtracting a score penalty
    private final double PREFERRED_MIN_LENGTH_PENALTY = 1;
    private final double PREFERRED_LENGTH = 250;
    private final double PREFERRED_LENGTH_BONUS = 1;
    private final long TIME_LIMIT = 1000;

    private List<PathTuple> queue;
    private Set<Long> visitedWays;

    @Autowired
    public BeamSearchConnectionPath(ElementRepo repo,
                                    @Qualifier("DistanceFromOriginToMidHeuristic") Heuristic distanceHeuristic,
                                    @Qualifier("FeaturesHeuristicMain") Heuristic featuresHeuristic,
                                    @Qualifier("EdgeDistanceCalculatorMain") EdgeDistanceCalculator edgeDistanceCalculator,
                                    @Qualifier("SimpleGradientCalculator") GradientCalculator gradientCalculator,
                                    @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        super(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        this.queue = new ArrayList<>();
        visitedWays = new HashSet<>();
    }


    /**
     * Method for generating a route of the specified length,
     * that selects a path based on the given preferences.
     * The method returns as soon as the target way is reached.
     *
     * @param originNode the starting node of of the connecting path
     * @param originWay the starting way of the connecting path
     * @param targetNode the target node of the connecting path
     * @param targetWay the target way of the connecting path
     * @param distance the total distance available to travel
     * @return a PathTuple that is the head of the linked list
     * of PathTuples containing the path back to the origin point
     */
    @Override
    public PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double distance, double REPLACETWO) {
        double currentRouteLength;
        visitedWays = new HashSet<>();
        this.queue = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        double upperBound = distance;

        // add the starting PathTuple of the chain, with no link
        queue.add(new PathTupleMain(null, originNode, originWay,
                0, 0, 0));

        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT) {
            queue.sort(Comparator
                    // sort by route segment score
                    .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());

            // reduce the size of the queue where it exceeds beam size
            if (queue.size() > BEAM_SIZE) {
                queue = queue.subList(0, BEAM_SIZE);
            }

            PathTuple topTuple = queue.get(0);
            queue.remove(0);

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode = topTuple.getPreviousNode();
            double score;
            currentRouteLength = topTuple.getTotalLength();

            // the route has reached the target
            if (currentWay.getId() == targetWay.getId()) {
                double finalDistance = edgeDistanceCalculator
                        .calculateDistance(currentNode, targetNode, targetWay);
                // create a new tuple representing the journey from the previous node to the final node
                PathTuple returnTuple = new PathTupleMain(topTuple, targetNode,
                        targetWay, 0, finalDistance, topTuple.getTotalLength() + finalDistance);
                return returnTuple;
            }

            // distance to origin point from the last explored way
            double lastDist = distanceFromOriginHeuristic.getScore(currentWay);

            // for each Way reachable from the current Way
            for (ConnectionPair pair : repo.getConnectedWays(currentWay)) {
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

                double currentDistanceScore
                        = distanceFromOriginHeuristic.getScore(selectedWay);

                // if the current distance score is higher the previous Way's, that
                // is it is closer, increase the score
                if (currentDistanceScore > lastDist) {
                    score += DISTANCE_FROM_ORIGIN_BONUS;
                }

                if (distanceToNext < PREFERRED_MIN_LENGTH) {
                    score -= PREFERRED_MIN_LENGTH_PENALTY;
                }

                if (distanceToNext >= PREFERRED_LENGTH) {
                    score += PREFERRED_LENGTH_BONUS;
                }

                double gradient = gradientCalculator.calculateGradient(currentNode, currentWay, connectingNode,
                        selectedWay, distanceToNext);

                // call private method to add scores
                score += addScores(selectedWay, gradient, RANDOM_REDUCER);

                // create a new tuple representing this segment and add to the list
                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, distanceToNext, currentRouteLength + distanceToNext);
                queue.add(toAdd);
                visitedWays.add(currentWay.getId());

                elapsedTime = (new Date()).getTime() - startTime;
            }
        }

        return new PathTupleMain(null, null, null, -10000000,
                -1, -1);
    }
}
