package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface PathTuple {
    public Node getPredecessor();
    public double getScore();
    public double getLength();
    public Way getCurrentWay();
}
