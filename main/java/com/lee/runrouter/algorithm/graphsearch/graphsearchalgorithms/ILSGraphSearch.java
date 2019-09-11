package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.HashSet;

/**
 *  An interface for the graph search algorithm variant for use
 *  with the ILS algoirthm. Should return a PathTuple that is the
 *  head of a linked list of path segments making up the route.
 */
public interface ILSGraphSearch {
    PathTuple connectPath(PathTuple origin, PathTuple target, double availableDistance,
                          double targetDistance);
    void setIncludedWays(HashSet<Long> includedWays);
    void resetVisitedNodes();
}
