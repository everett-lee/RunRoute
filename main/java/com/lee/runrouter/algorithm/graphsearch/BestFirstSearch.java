package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.*;
import com.lee.runrouter.graph.elementrepo.*;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;


import java.util.*;

public class BestFirstSearch implements GraphSearch {
    private ElementRepo repo;
    private Heuristic distanceFromOriginHeursitic;
    private Heuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;
    private ElevationHeuristic elevationHeuristic;
    private double maxGradient = 0.8;

    private PriorityQueue<PathTuple> queue;
    private final double SCALE = 0.05; // amount to scale upper and lower bound on
    // run length by

    public BestFirstSearch(ElementRepo repo, Heuristic distanceHeuristic,
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

        double runLength;
        double upperBound = distance + (distance * SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * SCALE); // lower bound of
        // run length

        Node currentNode = new Node(0, coords[0], coords[1]);
        // set starting node to the member of the root Way that is closest to it
        currentNode = AlgoHelpers.findClosest(currentNode, root.getNodeContainer().getNodes());

        // add the root Way to to the queue, with predecessor set to null
        queue.add(new PathTupleMain(null, currentNode, repo.getOriginWay(), 0, 0));

        while (!queue.isEmpty()) {
            PathTuple topTuple = queue.poll();

            // if the run has exceeded its minimum length
            if (topTuple.getLength() >= lowerBound) {
                // the route has returned to the origin
                if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {
                    returnPath(topTuple);
                    return topTuple;
                }
            }

            Way currentWay = topTuple.getCurrentWay();
            double score;

            // for each of the Ways reachable from the current Way
            for (ConnectionPair pair : repo.getConnectedWays(currentWay)) {
                runLength = topTuple.getLength();
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();
                score = 0;

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (runLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }

                double gradient = elevationHeuristic.getScore(currentNode, connectingNode,
                        currentWay, selectedWay, distanceToNext);

                if (Math.abs(gradient) > this.maxGradient) {
                    continue; }

                score += gradient;

                //  add the corresponding features score
                score += featuresHeuristic.getScore(selectedWay);

                if (runLength + distanceToNext > distance / 4) {
                    score += distanceFromOriginHeursitic.getScore(selectedWay);
                }

                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, runLength + distanceToNext);
                queue.add(toAdd);

                if (visitedWays.contains(selectedWay.getId())) {
                    score -= 1;
                }

                visitedWays.add(selectedWay.getId());
            }
        }

        // error condition
        return new PathTupleMain(null, null, repo.getOriginWay(), -1, -1);
    }

    public void setMaxGradient(double maxGradient) {
        this.maxGradient = maxGradient;
    }

    static void returnPath(PathTuple tp) {
        if (tp.getPredecessor() == null) {
            System.out.println();
            return;
        }

        System.out.print("(" + tp.getPreviousNode().getId() + " distance: " + tp.getLength() + ") ");
        returnPath(tp.getPredecessor());
    }
}
