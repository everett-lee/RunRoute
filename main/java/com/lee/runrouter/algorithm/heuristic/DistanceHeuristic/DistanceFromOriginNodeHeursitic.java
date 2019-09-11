package com.lee.runrouter.algorithm.heuristic.DistanceHeuristic;
import com.lee.runrouter.graph.graphbuilder.node.Node;

/**
 * Interface the distance-based heuristics. These calculate the distance
 * 'as the crow flies' between two Nodes and apply a score accordingly.
 */
public interface DistanceFromOriginNodeHeursitic {
    double getScore(Node currentNode, Node selectedNode, Node originNode,
                           double currentRouteLength, double targetDistance);
}
