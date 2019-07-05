package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.io.Serializable;

/**
 * Stores the path traversed in the course of the generated cycle
 * as a linked list.
 * The path can be re-traced by following predecessor nodes.
 */
public class PathTupleMain implements PathTuple, Serializable {
    private PathTuple predecessor;
    private Node previousNode;
    private Way currentWay;
    private double segmentScore;
    private double segmentLength;
    private double totalLength;
    private double totalScore;

    public PathTupleMain(PathTuple predecessor, Node previousNode, Way currentWay, double segmentScore,
                         double totalScore, double segmentLength, double totalLength) {
        this.predecessor = predecessor;
        this.previousNode = previousNode;
        this.currentWay = currentWay;
        this.segmentScore = segmentScore;
        this.segmentLength = segmentLength;
        this.totalLength = totalLength;
        this.totalScore = totalScore;
    }

    @Override
    public PathTuple getPredecessor() {
        return this.predecessor;
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

    @Override
    public double getSegmentScore() {
        return segmentScore;
    }

    @Override
    public void setSegmentScore(double segmentScore) {
        this.segmentScore = segmentScore;
    }

    @Override
    public double getSegmentLength() {
        return segmentLength;
    }

    @Override
    public void setSegmentLength(double segmentLength) {
        this.segmentLength = segmentLength;
    }

    @Override
    public double getTotalLength() {
        return totalLength;
    }

    @Override
    public void setTotalLength(double totalLength) {
        this.totalLength = totalLength;
    }

    @Override
    public double getTotalScore() {
        return this.totalScore;
    }

    @Override
    public void setTotalScore(double score) {
        this.totalScore = score;
    }
}
