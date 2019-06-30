package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface IteratedLocalSearch {
    public PathTuple searchGraph(PathTuple head, double targetDistance);
}
