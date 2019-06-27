package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AlgoHelpers {
    static DistanceCalculator distanceCalculator = new HaversineCalculator();

    // find the closest node and return it.
    static public Node findClosest(Node compared, List<Node> colleagues) {

        Node closest = colleagues.stream().sorted(Comparator
                .comparing(currentNode -> distanceCalculator.calculateDistance(compared, currentNode)))
                .findFirst()
                .orElse(null);

        return closest;
    }
}
