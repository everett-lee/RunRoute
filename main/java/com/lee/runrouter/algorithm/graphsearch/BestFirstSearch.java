package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.*;
import com.lee.runrouter.graph.elementrepo.*;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BestFirstSearch implements GraphSearch {
    private ElementRepo repo;
    private DistanceFromOriginHeuristic distanceHeursitic;
    private FeaturesHeuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;

    private PriorityQueue<PathTuple> queue;
    private final double SCALE = 0.05; // amount to scale upper and lower bound on
    // run length by

    public BestFirstSearch(ElementRepo repo, DistanceFromOriginHeuristic distanceHeuristic,
                           FeaturesHeuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator) {
        this.repo = repo;
        this.distanceHeursitic = distanceHeuristic;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;

        // compare priority queue items by their assigned score
        this.queue = new PriorityQueue<>(Comparator.comparing(node -> node.getScore()));
    }

    @Override
    public PathTuple searchGraph(Way root, double[] coords, double distance) {
        double runLength = 0;
        double upperBound = distance + (distance * SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * SCALE); // lower bound of
        // run length

        Node currentNode = new Node(0, coords[0], coords[1]);
        // set starting node to the member of the root Way that is closest to it
        currentNode = AlgoHelpers.findClosest(currentNode, root.getNodeContainer().getNodes());

        // add the root Way to to the queue, with predecessor set to null
        queue.add(new PrimaryPathTuple(null, repo.getOriginWay(), 0, 0));

        while (!queue.isEmpty()) {

            PathTuple topTuple = queue.poll();
            runLength = topTuple.getLength();
            Way currentWay = topTuple.getCurrentWay();
            double score = 0;

            // for each of the Ways reachable from the current Way
            for (ConnectionPair pair : repo.getConnectedWays(currentWay)) {
                score = 0;
                Node connectingNode = pair.getConnectingNode();
                Way selectedWay = pair.getConnectingWay();

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                if (runLength + distanceToNext > upperBound) {
                    continue; // skip to next where max lenght exceeded
                }

                //score += calculateElevationScore(currentNode, currentWay);
                score += featuresHeuristic.getScore(currentNode, connectingNode, selectedWay);


                // if the run has exceeded its minimum length
                if (runLength >= lowerBound) {
                    // the route has returned to the origin
                    if (currentWay == repo.getOriginWay()) {
                        return new PrimaryPathTuple(currentNode, selectedWay, score, runLength);
                    }
                }

            }

        }

        // error condition
        return new PrimaryPathTuple(currentNode, repo.getOriginWay(), -1, -1);
    }

    private double calculateElevationScore(Node currentNode, Way w) {
        return 1;
    }

}
