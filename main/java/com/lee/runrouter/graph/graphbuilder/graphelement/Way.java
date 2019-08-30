package com.lee.runrouter.graph.graphbuilder.graphelement;

import com.lee.runrouter.graph.graphbuilder.node.NodeContainer;

import java.io.Serializable;

/**
 * A representation of an OpenStreetMap Way. Contains fields holding tag info
 * along with the length, elevation at start and terminal nodes, as well as
 * each of the nodes composing the way.
 */
public class Way implements Serializable {
    public enum Surface {GRASS, DIRT, GRAVEL, UNPAVED, GROUND, EARTH, COBBLESTONE,
                        ASPHALT, CONCRETE, PAVING_STONES, SAND, UNDEFINED};
    public enum Highway {TRUNK, PRIMARY, SECONDARY, TERTIARY, UNCLASSIFIED,
                        RESIDENTIAL, LIVING_STREET, SERVICE, PEDESTRIAN, TRACK, ROAD,
                        FOOTWAY, BRIDLEWAY, STEPS, PATH, CYCLEWAY};

    private long id;
    private String name;
    private Surface surface;
    private Highway highway;
    private boolean isLit;
    private double length;
    private ElevationPair elevationPair;
    private NodeContainer nodeContainer;


    public Way(long id) {
        this.id = id;
        this.surface = Surface.UNDEFINED;
        this.highway = Highway.UNCLASSIFIED;
        this.name = "Unknown"; // some Ways are unnamed by default
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurface() {
        return surface.toString();
    }

    public void setSurface(String surface) {
        try {
            this.surface = Surface.valueOf(surface.toUpperCase());
        } catch (IllegalArgumentException e) {
            // value not contained in ENUM
            this.surface = Surface.UNDEFINED;
        }
    }

    public boolean isLit() {
        return isLit;
    }

    public void setLit(boolean lit) {
        isLit = lit;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public ElevationPair getElevationPair() {
        return elevationPair;
    }

    public void setElevationPair(ElevationPair elevationPair) {
        this.elevationPair = elevationPair;
    }

    public NodeContainer getNodeContainer() {
        return nodeContainer;
    }

    public void setNodeContainer(NodeContainer nodeContainer) {
        this.nodeContainer = nodeContainer;
    }

    public String getHighway() {
        return highway.toString();
    }

    public void setHighway(String highway) {
        try {
            this.highway = Highway.valueOf(highway.toUpperCase());
        } catch (IllegalArgumentException e) {
            // value not contained in ENUM
            this.highway = Highway.UNCLASSIFIED;
        }
    }

    @Override
    public String toString() {
        return String.format("id: %d, name: %s, surface: %s, highway %s, lit: %s, length: %.3f" +
                        "\n nodes: %s" +
                        "\n elevation: %s",
                this.id, this.name, this.surface, this.highway, this.isLit, this.length, this.nodeContainer.toString(), this.elevationPair.toString());
    }
}
