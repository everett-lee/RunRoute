package com.lee.runrouter.algorithm.pathnode;

import java.io.Serializable;

/** A pair storing both the path segment's score derived
 * from its distance to the origin point and its score
 * based on the heuristics.
 */
public class ScorePair implements Serializable {
    private double distanceScore;
    private double heuristicScore;

    public ScorePair(double distanceScore, double heuristicScore) {
        this.distanceScore = distanceScore;
        this.heuristicScore = heuristicScore;
    }

    public double getSum() {
        return this.heuristicScore + this.distanceScore;
    }

    public double getDistanceScore() {
        return distanceScore;
    }

    public double getHeuristicScore() {
        return heuristicScore;
    }
}
