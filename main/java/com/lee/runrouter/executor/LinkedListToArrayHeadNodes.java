package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("LinkedListToArrayHeadNodes")
// returns just the head node of each linked list element
public class LinkedListToArrayHeadNodes implements LinkedListToArray {

    // convert the returned route to an ArrayList, ready to be
    // sent to the client as JSON
    public List<Node> convert(PathTuple head) {
        List<Node> nodes = new ArrayList<>();

        while (head != null) {
            nodes.add(head.getPreviousNode());
            head = head.getPredecessor();
        }
        return nodes;
    }
}
