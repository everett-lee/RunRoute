package com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

public interface FeaturesHeuristic {
    public void getScore(Way selectedWay, double distance);
    public void setPreferredSurfaces(List<String> preferredSurfaces);
    public void setDislikedSurfaces(List<String> dislikedSurfaces);
    public void setPreferredHighways(List<String> preferredHighways);
}
