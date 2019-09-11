package com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface for calculating a score derived from the
 * features of a transited Way
 */
public interface FeaturesHeuristic {
    double getScore(Way selectedWay, double distance);

    void setPreferredSurfaces(List<String> preferredSurfaces);

    void setDislikedSurfaces(List<String> dislikedSurfaces);

    void setPreferredHighways(List<String> preferredHighways);
}
