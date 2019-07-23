package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Qualifier("LinkedListToArrayAllNodes")
// returns list of all nodes
public class LinkedListToArrayAllNodes implements LinkedListToArray{
    ElementRepo elementRepo;

    @Autowired
    public LinkedListToArrayAllNodes(ElementRepo elementRepo) {
        this.elementRepo = elementRepo;
    }

    // convert the returned route to an ArrayList, ready to be
    // sent to the client as JSON
    public List<Node> convert(PathTuple head) {
        List<Node> nodes = new ArrayList<>();

        while (head.getPredecessor() != null) {
            final long startId = head.getPreviousNode().getId(); // id of current node
            final long endId = head.getPredecessor().getPreviousNode().getId(); // id of next node

            Way currentWay = head.getCurrentWay();
//            for (Way way: elementRepo.getNodeToWay().get(startId)) {
//                // if the selected way contains the end node (in addition to the start node)
//                if (way.getNodeContainer().getNodes().stream().anyMatch( node -> node.getId() == endId)) {
//                    currentWay = way;
//                }
//            }

            List<Node> nodeContainer = currentWay.getNodeContainer().getNodes();

            int startNodeIndex = -1;
            int endNodeIndex = -1;
            for (int i = 0; i < nodeContainer.size(); i++) {
                if (nodeContainer.get(i).getId() == startId) {
                    startNodeIndex = i;
                }
                if (nodeContainer.get(i).getId() == endId) {
                    endNodeIndex = i;
                }
            }

            if (startNodeIndex < endNodeIndex) {
                for (int i = startNodeIndex; i <= endNodeIndex; i++) {
                    nodes.add(nodeContainer.get(i));
                }
            } else {
                for (int j = startNodeIndex; j >= endNodeIndex; j--) {
                    nodes.add(nodeContainer.get(j));
                }
            }
            head = head.getPredecessor();

        }


        return nodes;
    }
}
