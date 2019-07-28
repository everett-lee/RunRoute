package com.lee.runrouter.executor;

import java.util.List;
import com.lee.runrouter.graph.graphbuilder.node.Node;

/**
 * Object containing route nodes and other data
 * send back to the client in JSON format
 * following an API query
 */
public class ResponseObject {
    private List<Node> pathNodes;
    private double distance;
    private String startingWay;

    public ResponseObject(List<Node> pathNodes, double distance, String startingWay) {
        this.pathNodes = pathNodes;
        this.distance = distance;
        this.startingWay = startingWay;
    }


    public List<Node> getPathNodes() {
        return pathNodes;
    }

    public void setPathNodes(List<Node> pathNodes) {
        this.pathNodes = pathNodes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStartingWay() {
        return startingWay;
    }

    public void setStartingWay(String startingWay) {
        this.startingWay = startingWay;
    }
}
