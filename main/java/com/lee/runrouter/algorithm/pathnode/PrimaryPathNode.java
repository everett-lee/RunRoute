package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;


public class PrimaryPathNode implements PathNode {
    private Way predecessor;
    private Way currentWay;
    private double score;

    public PrimaryPathNode(Way predecessor, Way currentWay, double score) {
        this.predecessor = predecessor;
        this.currentWay = currentWay;
        this.score = score;
    }

    @Override
    public Way getPredecessor() {
        return this.predecessor;
    }

    @Override
    public Way getWay() {
        return this.currentWay;
    }

    @Override
    public double getScore() {
        return this.score;
    }
}
