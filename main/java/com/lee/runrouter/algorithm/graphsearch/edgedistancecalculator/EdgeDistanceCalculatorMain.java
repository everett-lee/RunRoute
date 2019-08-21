package com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * For calculating the great-circle distance between each of the points/Nodes
 * making up a given Way.
 */
@Component
@Qualifier("EdgeDistanceCalculatorMain")
public class EdgeDistanceCalculatorMain implements EdgeDistanceCalculator {
    DistanceCalculator distanceCalculator; // the calculator used to
    // estimate the great-circle distance between points

    @Autowired
    public EdgeDistanceCalculatorMain(
            @Qualifier("HaversineCalculator") DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public double calculateDistance(Node currentNode, Node connectingNode, Way currentWay) {
        // the current node and the connecting node are the first and last of the Way
        boolean firstAndLast = (currentNode.getId() == currentWay.getNodeContainer().getStartNode().getId())
                &&
                ((connectingNode.getId() == currentWay.getNodeContainer().getEndNode().getId()));

        // the current node and the connecting node are the last and first of the Way
        boolean lastAndFirst = (currentNode.getId() == currentWay.getNodeContainer().getEndNode().getId())
                &&
                ((connectingNode.getId() == currentWay.getNodeContainer().getStartNode().getId()));


        // in this case simply return the length of the way, as it is not intersected by
        // the connecting node
        if ((firstAndLast) || (lastAndFirst)) {
            return currentWay.getLength();
        }

        // otherwise calculate the distance based on the Way's nodes
        return iterativeDistance(currentNode, connectingNode, currentWay);
    }

    /**
     *  Used to calculate distance where the connecting Node intersects the current Way.
     *  In this case the default length of the Way cannot be used to measure the distance.
     *
     * @param currentNode the Node being departed from
     * @param connectingNode the Node travelled to. Connects current Way to the next.
     * @param currentWay the Way containing both current and connecting nodes.
     * @return double representing distance travelled in metres
     */
    private double iterativeDistance(Node currentNode, Node connectingNode, Way currentWay) {
        double distance = 0;

        List<Node> nodes = currentWay.getNodeContainer().getNodes();

        int indexofCurrentNode = -1;
        int indexOfConnectingNode = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getId() == currentNode.getId()) {
                indexofCurrentNode = i;
            }

            if (nodes.get(i).getId() == connectingNode.getId()) {
                indexOfConnectingNode = i;
            }
        }

        Node thisNode = currentNode;
        Node nextNode = null;
        // current node is at the front of the list, so iterate forwards
        if (indexOfConnectingNode > indexofCurrentNode) {
            int i = indexofCurrentNode;
            while (thisNode.getId() != connectingNode.getId()) {
                i += 1;
                nextNode = nodes.get(i);
                // add the distance between current node and the next
                double runningDist = distanceCalculator.calculateDistance(thisNode, nextNode);
                distance += runningDist;
                thisNode = nextNode;
            }

        // current node is at the back of the list, so iterate backwards
        } else {
            int i = indexofCurrentNode;
            while (thisNode.getId() != connectingNode.getId()) {
                i -= 1;
                nextNode = nodes.get(i);
                distance += distanceCalculator.calculateDistance(thisNode, nextNode);
                thisNode = nextNode;
            }
        }
        return distance;
    }
}
