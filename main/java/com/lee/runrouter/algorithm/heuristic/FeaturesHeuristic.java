package com.lee.runrouter.algorithm.heuristic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

public interface FeaturesHeuristic {
    public void setPreferredSurfaces(List<String> preferredSurfaces);
    public void setDislikedSurfaces(List<String> dislikedSurfaces);
    public void setPreferredHighways(List<String> preferredHighways);
}
