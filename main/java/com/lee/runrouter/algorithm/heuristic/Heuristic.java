package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.*;
import com.lee.runrouter.graph.graphbuilder.node.*;

public interface Heuristic {
    public double getScore(Way selectedWay);

}
