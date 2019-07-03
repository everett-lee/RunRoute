package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;

import java.nio.file.Path;
import java.util.Date;

public class IteratedLocalSearchMainII implements IteratedLocalSearch {
    private ILSGraphSearch graphSearch;
    private final long TIME_LIMIT = 3000L;

    public IteratedLocalSearchMainII(ILSGraphSearch graphSearch) {
        this.graphSearch = graphSearch;
    }

    @Override
    public PathTuple iterate(PathTuple head, double targetDistance) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        PathTuple start = null;
        PathTuple end = null;

        // begin by reversing the path
        head = reverseList(head);

        double remainingDistance = targetDistance; // distance left available
        // to add to route

      return null;
    }

    private PathTuple reverseList(PathTuple head) {
        PathTuple prev = null;
        PathTuple current = head;
        PathTuple next = null;

        while (current != null) {
            next = current.getPredecessor();
            current.setPredecessor(prev);
            prev = current;
            current = next;
        }
        return prev;
    }

    private PathTuple getStartPathSegment(PathTuple head, int a) {
        int i = 0;
        while (i < a - 1) {
            head = head.getPredecessor();
            i++;
        }
        return head;
    }

    private PathTuple getEndPathSegment(PathTuple endNode, int r) {
        int i = 0;

        if (endNode.getPreviousNode() == null) {
            return endNode;
        }

        while (i < r) {
            endNode = endNode.getPredecessor();
            i++;
        }
        return endNode;
    }

    private int getPathSize(PathTuple head) {
        int count = 0;

        while (head != null) {
            count++;
            head = head.getPredecessor();
        }

        return count;
    }

    private double calculateScore(PathTuple head, PathTuple tail) {
        double score = head.getSegmentScore();
        head = head.getPredecessor();

        while (head != tail) {
            score += head.getSegmentScore();
            head = head.getPredecessor();
        }

        return score;
    }

    private double calculateDistance(PathTuple head, PathTuple tail) {
        double distance = 0;
        head = head.getPredecessor();

        while (head != tail) {
            distance += head.getSegmentLength();
            head = head.getPredecessor();
        }

        return distance;
    }

    private PathTuple insertSegment(PathTuple start, PathTuple end, PathTuple newSegment) {
        newSegment = reverseList(newSegment); // reverse the segment to be added, as it
        // is currently in the wrong order


        start.setPredecessor(newSegment.getPredecessor()); // start of segment links to
        // new segment's next link (head of new segment is currently the same as the start
        // head


        PathTuple newSegmentTail = newSegment;
        while (newSegmentTail.getPredecessor() != null) {
            newSegmentTail = newSegmentTail.getPredecessor();
        }

        newSegmentTail.setPredecessor(end.getPredecessor()); // tail of new segment is linked
        // to next link of the head of the remaining existing path.
        end.setPredecessor(null);

        return start;
    }

    private void PRINTIT(PathTuple head) {
        while (head != null) {
            System.out.println(head.getPreviousNode() + " LENGTH " + head.getSegmentLength());
            head = head.getPredecessor();
        }
    }


}
