package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

public interface PathNode {
    public Way getPredecessor();
    public Way getWay();
    public double getScore();
}
