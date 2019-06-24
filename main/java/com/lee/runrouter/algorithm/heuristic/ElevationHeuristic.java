package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public class ElevationHeuristic implements Heuristic {
    private ElementRepo repo;

    public ElevationHeuristic(ElementRepo repo) {
        this.repo = repo;
    }

    @Override
    public double getScore(Node currentNode,Node visitedNode, Way selectedWay) {
        return 0;
    }


    // simple rise over run calculation
    private double calculateIncline(double startElevation, double endElevation, double distance) {
        return (endElevation-startElevation) / distance;
    }

}
