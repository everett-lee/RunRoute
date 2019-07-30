package com.lee.runrouter.algorithm.heuristic;

import java.util.List;

public interface FeaturesHeuristic {
    public void setPreferredSurfaces(List<String> preferredSurfaces);
    public void setDislikedSurfaces(List<String> dislikedSurfaces);
    public void setPreferredHighways(List<String> preferredHighways);
}
