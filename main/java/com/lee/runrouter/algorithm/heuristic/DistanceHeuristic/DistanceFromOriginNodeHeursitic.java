package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface DistanceFromOriginNodeHeursitic {
    double getScore(Node currentNode, Node selectedNode, Node originNode,
                           double currentRouteLength, double targetDistance);
}
