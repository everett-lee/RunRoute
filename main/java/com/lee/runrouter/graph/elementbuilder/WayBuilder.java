package com.lee.runrouter.graph.elementbuilder;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.graph.graphbuilder.graphelement.ElevationPair;
import com.lee.runrouter.graph.graphbuilder.graphelement.NodeContainer;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for generating Way instances. Each field is set
 * corresponding to the result of a row in the PostGIS SQL query.
 */
@Component
public class WayBuilder {
    Way wayInstance;

    public void reset() {
        this.wayInstance = null;
    }

    public void createInstance(long id) {
        this.reset();
        wayInstance = new Way(id);
    }

    public void setName(String name) {
        wayInstance.setName(name);
    }

    public void setAsLit() {
        wayInstance.setLit(true);
    }

    public void setSurface(String surface) {
        wayInstance.setSurface(surface);
    }

    public void setHighWay(String highway) {
        wayInstance.setHighway(highway);
    }

    public void setLength(double length) {
        wayInstance.setLength(length);
    }

    public void setElevationPair(long startElevation, long endElevation) {
        wayInstance.setElevationPair(new ElevationPair(startElevation, endElevation));
    }

    public void setNodes(List<Long> nodes, List<List<Double>> coords) {
        Node startNode = null;
        Node endNode = null;
        List<Node> nodeList = new ArrayList<>();

        // iterate over the list of nodes and coords and create an instance
        // using data from each
        for (int i = 0; i < nodes.size(); i++) {
            long id = nodes.get(i);
            double lat = coords.get(i).get(1);
            double lon = coords.get(i).get(0);

            Node node = new Node(id, lat, lon);
            nodeList.add(node);

            // start node is first in list
            if (i == 0) {
                startNode = node;
            }

            // end node is first in list
            if (i == nodeList.size()- 1) {
                endNode = node;
            }
        }
        wayInstance.setNodeContainer(new NodeContainer(startNode, endNode, nodeList));
    }

    public Way getElement() {
        return this.wayInstance;
    }
}
