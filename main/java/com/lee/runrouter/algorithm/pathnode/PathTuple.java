package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface PathTuple {
    public PathTuple getPredecessor();
    public double getScore();
    public double getLength();
    public Node getPreviousNode();
    public Way getCurrentWay();
    public void setPredecessor(PathTuple predecessor);
}
