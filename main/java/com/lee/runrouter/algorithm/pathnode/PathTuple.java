package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

public interface PathTuple {
    public Way getPredecessor();
    public Way getWay();
    public double getScore();
    public double getLength();
}
