package com.lee.runrouter.algorithm.heuristic;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface DistanceFromOriginNodeHeursitic {
    public double getScore(Node currentNode, Node originNode,
                           double currentRouteLength, double targetDistance);
}
