package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface PathTuple {
    PathTuple getPredecessor();
    Node getPreviousNode();
    Way getCurrentWay();
    void setPredecessor(PathTuple predecessor);
    double getSegmentScore();
    void setSegmentScore(double segmentScore);
    double getSegmentLength();
    void setSegmentLength(double segmentLength);
    double getTotalLength();
    void setTotalLength(double totalLength);
    double getTotalScore();
    void setTotalScore(double score);
}
