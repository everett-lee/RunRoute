package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

/**
 * Stores the path traversed in the course of the generated cycle.
 * The path can be re-traced by following predecessor nodes.
 */
public class PathTupleMain implements PathTuple {
    private PathTuple predecessor;
    private Node previousNode;
    private Way currentWay;
    private double score;
    private double length;

    public PathTupleMain(PathTuple predecessor, Node previousNode, Way currentWay, double score, double length) {
        this.predecessor = predecessor;
        this.previousNode = previousNode;
        this.currentWay = currentWay;
        this.score = score;
        this.length = length;
    }

    @Override
    public PathTuple getPredecessor() {
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
    public Node getPreviousNode() {
        return this.previousNode;
    }

    @Override
    public Way getCurrentWay() {
        return this.currentWay;
    }

    @Override
    public void setPredecessor(PathTuple predecessor) {
        this.predecessor = predecessor;
    }


}
