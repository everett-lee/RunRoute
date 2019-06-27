package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ConnectionPair;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;


public class ReturnPath implements GraphSearch {
    private ElementRepo repo;
    private Heuristic distanceFromOriginHeursitic;
    private Heuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;
    private ElevationHeuristic elevationHeuristic;
    private DistanceCalculator distanceCalculator;
    private double maxGradient = 0.1;

    private PriorityQueue<PathTuple> queue;
    private final double SCALE = 0.5; // amount to scale upper and lower bound on
    // run length by

    public ReturnPath(ElementRepo repo, Heuristic distanceHeuristic,
                      Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator,
                      ElevationHeuristic elevationHeuristic, DistanceCalculator distanceCalculator) {
        this.repo = repo;
        this.distanceFromOriginHeursitic = distanceHeuristic;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;
        this.distanceCalculator = distanceCalculator;

        // compare priority queue items by their assigned score in descending order
        this.queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getScore()).reversed());
    }


    @Override
    public PathTuple searchGraph(Way root, double[] coords, double distance) {
        distance *= 1000; // distance in meters
        Set<Long> visitedWays = new HashSet<>();

        double currentRouteLength;
        double upperBound = distance + (distance * SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * SCALE); // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, root.getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root, 0, 0));

        while (!queue.isEmpty()) {
            PathTuple topTuple = queue.poll();

            Way currentWay = topTuple.getCurrentWay();
            Node currentNode  = topTuple.getPreviousNode();
            double score;
            currentRouteLength = topTuple.getLength();

            // distance to origin point from the last explored way
            double lastDist = distanceFromOriginHeursitic.getScore(currentNode, currentNode, currentWay);

            // if the run has reached or exceeded its minimum length
            if (topTuple.getLength() >= lowerBound) {

                // the route has returned to the origin
                if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {
                    return topTuple;
                }
            }
            // for each Way reachable from the current Way
            for (ConnectionPair pair: repo.getConnectedWays(currentWay)) {
                currentRouteLength = topTuple.getLength();
                score = 0;
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (currentRouteLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }

                double currentDistanceScore
                        = distanceFromOriginHeursitic.getScore(currentNode, currentNode, selectedWay);

                // if the current distance score is less than the previous Way's, that
                // is it is further away, then skip this iteration
                if (currentDistanceScore < lastDist) {
                    continue;
                }

                // drop the score where this way has already been explored
                if (visitedWays.contains(currentWay.getId())) {
                    score -= 1;
                }

                visitedWays.add(currentWay.getId());

                score += elevationHeuristic.getScore(currentNode,
                        connectingNode, currentWay, selectedWay, distanceToNext);

                score += featuresHeuristic.getScore(currentNode, connectingNode, selectedWay);

                // add a small random value to break ties
                score += (Math.random()/10);

                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, currentRouteLength + distanceToNext);
                queue.add(toAdd);
            }

        }

        return new PathTupleMain(null, null, null,
                -1, -1);
    }

    static void returnPath(PathTuple tp) {
        if (tp.getPredecessor() == null) {
            System.out.println();
            return;
        }

        System.out.print("(" + tp.getPreviousNode().getId() + " distance: " + tp.getLength() + ") " + "Way : "
                + tp.getCurrentWay().getId());
        returnPath(tp.getPredecessor());
    }
 }
