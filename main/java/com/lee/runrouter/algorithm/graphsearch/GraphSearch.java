package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

public interface GraphSearch {
    public PathTuple searchGraph(Way root, double[] coords, double distance);
}
