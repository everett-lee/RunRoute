package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.*;
import com.lee.runrouter.graph.graphbuilder.node.*;

public interface Heuristic {
    public double getScore(Node currentNode, Node visitedNode, Way selectedWay);

}
