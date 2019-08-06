package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import org.springframework.stereotype.Component;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CycleRemover {
    DistanceCalculator distanceCalculator;
    public CycleRemover(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    public double removeCycle (List <Node> nodes) {
        double distance = 0;

        if (nodes.size() < 3) {
            return 0;
        }

        Map<Long, Integer> idToPositionLookUp = new HashMap<>();
        int listSize = nodes.size();
        int maxCycleLength = 15;

        // starting from four in
        for (int i = 3; i < listSize; i++) {
            long currentNodeId = nodes.get(i).getId();

            if (idToPositionLookUp.containsKey(currentNodeId)) {

                // previous index of this node in the list
                int previousIndex = idToPositionLookUp.get(currentNodeId);

                // if the gap between these nodes is below the minimum
                if (i - previousIndex < maxCycleLength) {
                    int gapSize = i - previousIndex;

                    // remove nodes between the gap
                    for (int j = 0; j < gapSize; j++) {
                        int start = previousIndex +1;

                        // add distance between these nodes to removed distance
                        Node previousNode = nodes.get(start-1);
                        Node removedNode = nodes.get(start);
                        distance +=
                                distanceCalculator.calculateDistance(previousNode, removedNode);

                        nodes.remove(start);
                    }
                    // remove number of deleted from the list
                    listSize -= gapSize;
                }

            } else {
                idToPositionLookUp.put(currentNodeId, i);
            }
        }

        return distance;
    }
}
