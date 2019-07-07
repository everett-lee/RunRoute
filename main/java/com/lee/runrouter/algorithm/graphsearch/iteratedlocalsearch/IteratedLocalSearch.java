package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface IteratedLocalSearch {
    public PathTuple iterate(PathTuple head, double targetDistance);
    public int getIterations();
    public void setIterations(int iterations);
    public int getImprovements();
    public void setImprovements(int improvements);
}
