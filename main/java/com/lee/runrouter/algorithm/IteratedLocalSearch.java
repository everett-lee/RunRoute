package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.graphsearch.GraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

import java.util.Arrays;

public abstract class IteratedLocalSearch {
    private GraphSearch initialOutwardsPather; // generates the initial path
    // away from the origin point
    private GraphSearch initialReturnPather; // generates the initial path
    // back to the origin point
    private ElementRepo repo;

    public IteratedLocalSearch(GraphSearch initialOutwardsPather, GraphSearch initialReturnPather,
                               ElementRepo repo) {
        this.initialOutwardsPather = initialOutwardsPather;
        this.initialReturnPather = initialReturnPather;
        this.repo = repo;
    }

    public PathTuple generateCycle(double[] coords, double distance) {
        distance /= 2; // half distance for outward and return paths

        PathTuple outwardPath =
                initialOutwardsPather.searchGraph(repo.getOriginWay(), coords, distance);

        Way lastVisited = outwardPath.getCurrentWay();

        System.out.println("first half done");

        double[] lastVisitedCoords = {outwardPath.getPreviousNode().getLat(),
        outwardPath.getPreviousNode().getLon()};

        PathTuple returnPath = initialReturnPather.searchGraph(lastVisited, lastVisitedCoords, distance);
//
//        PathTuple tail = returnPath;
//        while (tail.getPredecessor() != null) {
//            tail = tail.getPredecessor();
//        }
//
//        // link outbound and return journeys
//        tail.setPredecessor(outwardPath);

        return returnPath;
    }
}
