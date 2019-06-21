package com.lee.runrouter.graph.elementrepo;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A repository class for generated Way instances. Used by the
 * routing algorithm to look up connected Ways
 */
@Component
public class ElementRepo {
    private Way originWay;
    private final Way start; // starting node of the route
    private final Map<Long, List<Way>> nodeToWay; // the Ways associated
    // with each node ID
    private final List<Way> wayRepo; // contains all generated Ways

    public ElementRepo() {
        this.start = null;
        this.nodeToWay = new HashMap<>();
        this.wayRepo = new ArrayList<>();
    }

    public Map<Long, List<Way>> getNodeToWay() {
        return nodeToWay;
    }

    public void addNodeToWay(long id, Way way) {
        if (! nodeToWay.containsKey(id)) { // where no Way is currently
            // associated with the id
            nodeToWay.put(id, new ArrayList<>(Arrays.asList(way)));
        } else {
            nodeToWay.get(id).add(way); // add the associated way to the list
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
}
