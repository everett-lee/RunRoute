package com.lee.runrouter.queryexecutor;

import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.List;

public interface ClientQueryExecutor {
    public List<Node> executeQuery(double[] coords, double length);
}
