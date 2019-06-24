package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

/**
 * Stores the path traversed in the course of the generated cycle.
 * The path can be re-traced by following predecessor nodes.
 */
public class PrimaryPathTuple implements PathTuple {
    private Node predecessor;
    private Way currentWay;
    private double score;
    private double length;

    public PrimaryPathTuple(Node predecessor, Way currentWay, double score, double length) {
        this.predecessor = predecessor;
        this.currentWay = currentWay;
        this.score = score;
        this.length = length;
    }

    @Override
    public Node getPredecessor() {
        return this.predecessor;
    }

    @Override
    public double getScore() {
        return this.score;
    }

    @Override
    public double getLength() {
        return this.length;
    }

    @Override
    public Way getCurrentWay() {
        return this.currentWay;
    }
}
