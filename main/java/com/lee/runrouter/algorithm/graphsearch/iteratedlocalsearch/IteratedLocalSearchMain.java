package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.graphsearch.GraphSearch;
import com.lee.runrouter.algorithm.graphsearch.cyclegenerator.CycleGenerator;
import com.lee.runrouter.algorithm.pathnode.PathTuple;

public class IteratedLocalSearchMain implements IteratedLocalSearch {
    public IteratedLocalSearchMain() {
    }

    @Override
    public PathTuple searchGraph(PathTuple head, double targetDistance) {
        PathTuple solution = head;
        int a = 1; // the starting node, indexed from 1
        int r = 1; // number of nodes to remove
        int pathLength = getPathSize(head);

        // reset if greater than pathLength minus the start and end node
        if (r > pathLength - 2) {
            r = 1;
        }

        // reset if r is removed section minus the start node is greater
        // than the number of nodes
        if (a + r > pathLength - 1) {
            r = Math.max(1, pathLength - 1 - a);
        }

        // reset when end of path is reached
        if (a == r-1) {
            a = r = 1;
        }


        return  null;
    }

    /**
     * Returns the node at position a, which will preceed the segment
     * being removed
     *
     * @param head the head node of the segment
     * @param a    the position of the node
     * @return the head node
     */
    private PathTuple getStartPathSegment(PathTuple head, int a) {
        int i = 0;

        while (i < a - 1) {
            head = head.getPredecessor();
            i++;
        }
        return head;
    }


    /**
     * returns the end node, that is preceeded by the segment being removed
     *
     * @param endNode
     * @param r       an integer representing the size of the segment being removed
     * @return the end node of the segment
     */
    private PathTuple getEndPathSegment(PathTuple endNode, int r) {
        int i = 0;

        while (i <= r) {
            endNode = endNode.getPredecessor();
            i++;
        }
        return endNode;
    }

    /**
     * @param head head node of path being iterated over
     * @return the length of (number of nodes in) the path
     * as an integer
     */
    private int getPathSize(PathTuple head) {
        int count = 0;

        while (head != null) {
            head = head.getPredecessor();
            count += 1;
        }
        return count;
    }


}
