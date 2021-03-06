package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import java.util.List;

/**
 * Interface for converting the linked list
 * of PathTuples to any array
 */
public interface LinkedListToArray {
    List<Node> convert(PathTuple head);
}
