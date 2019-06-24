package com.lee.runrouter.graph.elementrepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
/**
 * A pair for storing a Way and the node that
 * connects it
 */
public class ConnectionPair {
    private final Way connectingWay;
    private final Node connectingNode;

    public ConnectionPair(Node connectingNode, Way connectingWay) {
        this.connectingNode = connectingNode;
        this.connectingWay = connectingWay;
    }

    public Way getConnectingWay() {
        return connectingWay;
    }

    public Node getConnectingNode() {
        return connectingNode;
    }
}
