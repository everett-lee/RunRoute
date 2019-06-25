package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface ElevationHeuristic {
    double getScore(Node currentNode, Node visitedNode, Way startingWay, Way selectedWay, double distance);

}
