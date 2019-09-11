package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.*;

/**
 * Interface for deriving a score from the features
 * of the input Way
 */
public interface Heuristic {
    double getScore(Way selectedWay);

}
