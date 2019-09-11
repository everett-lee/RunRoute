package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

/**
 *  An interface for the graph search algorithm. Should return a
 *  PathTuple that is the head of a linked list of path segments
 *  making up the route
 */
public interface GraphSearch {
    PathTuple searchGraph(Way root, double[] coords, double distance);
    void setTimeLimit(long timeLimit);
}
