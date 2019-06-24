package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;


public class PrimaryPathTuple implements PathTuple {
    private Way predecessor;
    private Way currentWay;
    private double score;
    private double length;

    public PrimaryPathTuple(Way predecessor, Way currentWay, double score, double length) {
        this.predecessor = predecessor;
        this.currentWay = currentWay;
        this.score = score;
        this.length = length;
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

    @Override
    public double getLength() {
        return this.length;
    }
}
