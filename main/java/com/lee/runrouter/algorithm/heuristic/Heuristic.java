package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.*;
import com.lee.runrouter.graph.graphbuilder.node.*;

import java.util.List;

public interface Heuristic {
    public double getScore(Way currentWay, Node currentNode, Way selectedWay);
    public void setHighwayOptions(boolean[] options);
}
