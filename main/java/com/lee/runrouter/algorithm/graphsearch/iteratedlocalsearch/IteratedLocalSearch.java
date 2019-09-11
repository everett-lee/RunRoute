package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.pathnode.PathTuple;

/**
 * An interface for producing the Iterated Local Search algorithm
 */
public interface IteratedLocalSearch {
    PathTuple iterate(PathTuple head, double targetDistance);

    int getIterations();

    void setIterations(int iterations);

    int getImprovements();

    void setImprovements(int improvements);
}
