package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface ConnectPathGraphSearch extends GraphSearch {
    public void setCurrentDistance(double distance);
    public void setTarget(Node targetNode, Way targetWay);
}
