package com.lee.runrouter.graph.elementrepo;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A repository class for generated Way instances. Used by the
 * routing algorithm to look up connected Ways
 */
@Component
public class ElementRepo implements Serializable {
    private Way originWay;
    private Node originNode; // starting node of the route
    private Map<Long, List<Way>> nodeToWay; // the Ways associated
    // with each node ID
    private List<Way> wayRepo; // contains all generated Ways
    private Hashtable<Long, Integer> visitedWays;
    private Hashtable<Long, Integer> visitedNodes;

    public ElementRepo() {
        this.originNode = null;
        this.nodeToWay = new HashMap<>();
        this.wayRepo = new ArrayList<>();
        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();
    }


    /** Examines siblings of a Way to return those others
     * that connect/intersect it
     *
     ** @param way being examined for connections
     * @return A list of connected Ways
     */
    public List<ConnectionPair> getConnectedWays(Way way) {
        List<ConnectionPair> connectedWays = new ArrayList<>();

        for (Node n: way.getNodeContainer().getNodes()) {
            // find all connected Ways using id number as look-up
            Optional<List<Way>> waysOptional = Optional.ofNullable(nodeToWay.get(n.getId()));

            if (waysOptional.isPresent()) {
                List<Way> ways = waysOptional.get();


                // for each way in the list of returned ways, add it to the list of connected Ways
                // along with the connected Node
                for (Way w: ways) {
                    if (w.getId() != way.getId()) {
                        connectedWays.add((new ConnectionPair(n, w)));
                    }
                }
            }
        }

        return connectedWays;
    }

    public Map<Long, List<Way>> getNodeToWay() {
        return nodeToWay;
    }

    public void addNodeToWay(long id, Way way) {
        if (! nodeToWay.containsKey(id)) { // where no Way is currently
            // associated with the id
            nodeToWay.put(id, new ArrayList<>(Arrays.asList(way)));
        } else {
            if (!nodeToWay.get(id).contains(way)) {
                nodeToWay.get(id).add(way); // add the associated way to the list
            }
        }
    }

    public List<Way> getWayRepo() {
        return wayRepo;
    }

    public Way getOriginWay() {
        return originWay;
    }

    public void setOriginWay(Way originWay) {
        this.originWay = originWay;
    }

    public Node getOriginNode() {
        return originNode;
    }

    public void setOriginNode(Node originNode) {
        this.originNode = originNode;
    }

    public void reset() {
        this.originNode = null;
        this.nodeToWay = new HashMap<>();
        this.wayRepo = new ArrayList<>();
        this.visitedWays = new Hashtable<>();
        this.visitedNodes = new Hashtable<>();
    }

    public Hashtable<Long, Integer> getVisitedWays() {
        return visitedWays;
    }

    public Hashtable<Long, Integer> getVisitedNodes() {
        return visitedNodes;
    }
}
