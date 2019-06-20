package com.lee.runrouter.graph.graphbuilder.node;

public class Node {
    private long id;
    private double lat;
    private double lon;

    public Node(long id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String toString() {
        return String.format("id: %d, coordinates: (%.3f, %.3f)", id, lat, lon);
    }
}
