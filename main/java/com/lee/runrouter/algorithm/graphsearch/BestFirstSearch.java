package com.lee.runrouter.algorithm.graphsearch;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BestFirstSearch implements GraphSearch {
    private ElementRepo repo;
    private DistanceFromOriginHeuristic distanceHeursitic;
    private FeaturesHeuristic featuresHeuristic;
    private PriorityQueue<PathTuple> queue;
    private final double SCALE = 0.05; // amount to scale upper and lower run length
    // by

    public BestFirstSearch(ElementRepo repo, DistanceFromOriginHeuristic distanceHeuristic,
                           FeaturesHeuristic featuresHeuristic) {
        this.repo = repo;
        this.distanceHeursitic = distanceHeuristic;
        this.featuresHeuristic = featuresHeuristic;

        // compare priority queue items by their assigned score
        this.queue = new PriorityQueue<>(Comparator.comparing(x -> x.getScore()));
    }

    @Override
    public PathTuple searchGraph(Way root, double[] coords, double distance) {
        double runLength = 0;
        double upperBound = distance + (distance * SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * SCALE); // lower bound of
        // run length

        while (!queue.isEmpty() && runLength <= upperBound) {

            Way currentWay = queue.poll().getWay();

            // if the run has exceeded its minimum length
            if (runLength > lowerBound) {
                // the route has returned to the origin
                if (currentWay == repo.getOriginWay()) {
                    break;
                }
            }

        }

    }

}
