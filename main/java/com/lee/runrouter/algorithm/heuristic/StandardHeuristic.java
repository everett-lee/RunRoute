package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;


public class StandardHeuristic implements Heuristic {
    private double score;
    private boolean[] options;

    @Override
    public double getScore(Way currentWay, Node currentNode, Way selectedWay) {
        return 0;
    }

    @Override
    public void setHighwayOptions(boolean[] options) {
        this.options = options;
    }
}
