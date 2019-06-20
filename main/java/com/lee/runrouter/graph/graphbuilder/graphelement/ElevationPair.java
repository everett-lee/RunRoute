package com.lee.runrouter.graph.graphbuilder.graphelement;

public class ElevationPair {
    private long startElevation;
    private long endElevation;

    public ElevationPair(long startElevation, long endElevation) {
        this.startElevation = startElevation;
        this.endElevation = endElevation;
    }

    public long getStartElevation() {
        return startElevation;
    }

    public long getEndElevation() {
        return endElevation;
    }

    @Override
    public String toString() {
        return String.format("start: %d, end: %d", this.startElevation, this.endElevation);
    }
}
