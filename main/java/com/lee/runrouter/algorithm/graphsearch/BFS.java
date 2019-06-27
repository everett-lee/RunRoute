package com.lee.runrouter.algorithm.graphsearch;


import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.*;
import com.lee.runrouter.graph.elementrepo.*;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.*;

public class BFS implements GraphSearch {
    private ElementRepo repo;
    private Heuristic distanceFromOriginHeursitic;
    private Heuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;
    private ElevationHeuristic elevationHeuristic;
    private double maxGradient = 0.8;

    private PriorityQueue<PathTuple> queue;
    private final double SCALE = 0.05; // amount to scale upper and lower bound on
    // run length by

    public BFS(ElementRepo repo, Heuristic distanceHeuristic,
               Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator,
               ElevationHeuristic elevationHeuristic) {
        this.repo = repo;
        this.distanceFromOriginHeursitic = distanceHeuristic;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;

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
        double lowerBound = distance; // lower bound of
        // run length

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        queue.add(new PathTupleMain(null, originNode, root, 0, 0));


        while (!queue.isEmpty()) {
            PathTuple topTuple = queue.poll();
            Way currentWay = topTuple.getCurrentWay();
            Node currentNode  = topTuple.getPreviousNode();
            double score;

            currentRouteLength = topTuple.getLength();

            if (currentRouteLength > lowerBound) {
                return topTuple;
            }

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

                // drop the score where this way has already been explored
                if (visitedWays.contains(currentWay.getId())) {
                    score -= 1;
                }

                visitedWays.add(currentWay.getId());

                double gradient = elevationHeuristic.getScore(currentNode, connectingNode,
                        currentWay, selectedWay, distanceToNext);

                if (Math.abs(gradient) > this.maxGradient) {
                    continue; }

                // add score reflecting correspondence of terrain features to user selections
                score += featuresHeuristic.getScore(selectedWay);

                // add a small random value to break ties
                score += (Math.random()/5);

                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, currentRouteLength + distanceToNext);
                queue.add(toAdd);
            }

        }

        return new PathTupleMain(null, null, null,
                -1, -1);
    }
}
