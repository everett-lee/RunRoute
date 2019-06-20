package com.lee.runrouter.graph.graphbuilder.graphelement;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import java.util.List;

/**
 * Contains the first and final node of each Way, along with
 * all the nodes with Compose the way.
 */
public class NodeContainer {
    private Node startNode;
    private Node endNode;
    private List<Node> nodes;

    public NodeContainer(Node startNode, Node endNode, List<Node> nodes) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.nodes = nodes;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return String.format("start: %s, end: %s, " +
                "\nnodes: ", this.startNode, this.endNode) + this.nodes.toString();
    }
}
