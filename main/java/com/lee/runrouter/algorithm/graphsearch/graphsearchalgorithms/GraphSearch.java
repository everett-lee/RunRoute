package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

public interface GraphSearch {
    public PathTuple searchGraph(Way root, double[] coords, double distance);
    void setTimeLimit(long timeLimit);
}
