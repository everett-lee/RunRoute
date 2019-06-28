package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.graphsearch.GraphSearch;
import com.lee.runrouter.graph.elementrepo.ElementRepo;

public class IteratedLocalSearchMain extends IteratedLocalSearch {
    public IteratedLocalSearchMain(GraphSearch initialOutwardsPather, GraphSearch initialReturnPather, ElementRepo repo) {
        super(initialOutwardsPather, initialReturnPather, repo);
    }
}
