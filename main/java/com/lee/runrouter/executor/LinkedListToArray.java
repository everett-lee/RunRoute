package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import java.util.List;

public interface LinkedListToArray {
    public List<Node> convert(PathTuple head);
}
