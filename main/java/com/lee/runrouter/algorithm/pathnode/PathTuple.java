package com.lee.runrouter.algorithm.pathnode;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface PathTuple {
    PathTuple getPredecessor();
    Node getCurrentNode();
    Way getCurrentWay();
    void setPredecessor(PathTuple predecessor);
    ScorePair getSegmentScore();
    void setSegmentScore(ScorePair segmentScore);
    double getSegmentLength();
    void setSegmentLength(double segmentLength);
    double getTotalLength();
    void setTotalLength(double totalLength);
    double getSegmentGradient();
}
