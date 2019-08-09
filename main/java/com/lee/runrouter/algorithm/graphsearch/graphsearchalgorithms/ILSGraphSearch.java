package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface ILSGraphSearch {
    PathTuple connectPath(Node originNode, Way originWay, Node targetNode, Way targetWay,
                                 double availableDistance, double initialDistance, double targetDistance);
    void resetVisitedNodes();
    void setMinimumPathPercentage(double minimumPathPercentage);
}
