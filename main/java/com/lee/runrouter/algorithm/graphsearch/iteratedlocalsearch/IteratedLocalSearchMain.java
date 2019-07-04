package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import java.util.Date;

public class IteratedLocalSearchMain implements IteratedLocalSearch {
    private ILSGraphSearch graphSearch;
    private final long TIME_LIMIT = 5000L;

    public IteratedLocalSearchMain(ILSGraphSearch graphSearch) {
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

        int a = 1; // the starting node, indexed from 1
        int r = 2; // number of nodes to remove
        while (elapsedTime <= TIME_LIMIT && remainingDistance > 0) {
            elapsedTime = (new Date()).getTime() - startTime;

            // get the number of nodes in the the path
            int pathSize = getPathSize(head);
            // reset if r greater than pathLength minus the start and end node
            if (r > pathSize - 2) {
                r = 2;
            }

            // reset r if removed section plus index of the
            // start node is greater than the number of nodes
            if (a + r > pathSize - 2) {
                r = Math.max(2, pathSize - 1 - a);
            }

            // reset a and r if a plus section to remove
            // extends past the final node
            if (a >= pathSize - 2) {
                a = 1;
                r = 2;
            }

            System.out.println("a ->>>> " + a);
            System.out.println("r ->>>> " + r);

            start = getStartPathSegment(head, a);
            end = getEndPathSegment(start, r);

            // calculate the length in metres of the segment to be removed, and
            // add to remaining distance
            double exisitingSegmentLength = calculateDistance(start, end); // does not include
            // start, includes end
            remainingDistance += exisitingSegmentLength;


            // generate the new segment
            PathTuple newSegment = graphSearch.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                    end.getPreviousNode(), end.getCurrentWay(), remainingDistance);

            double oldSegmentScore = calculateScore(start, end);
            double newSegmentScore = calculateScore(newSegment, null);

            if (oldSegmentScore > newSegmentScore || (newSegment == null)
                    || newSegment.getSegmentLength() == -1) {
                a++;
                r++;

                //add back the length in metres of the segment length as it was not removed
                remainingDistance -= exisitingSegmentLength;

                // new segment score is higher, so replace old path segment with the new one
            } else {
                // remove the added distance from the remaining
                remainingDistance -= calculateDistance(newSegment, null);

                insertSegment(start, end, newSegment);
                a = 1;
                r = 2;
            }
        }
        return head;
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
        if (head == tail) {
            return 0;
        }

        double score = 0;
        head = head.getPredecessor();

        while (head != tail) {
            score += head.getSegmentScore();
            head = head.getPredecessor();
        }


        if (tail != null) {
            score += tail.getSegmentScore();
        }
        return score;
    }

    private double calculateDistance(PathTuple head, PathTuple tail) {
        if (head == tail) {
            return 0;
        }

        double distance = 0;
        head = head.getPredecessor();

        while (head != tail) {
            distance += head.getSegmentLength();
            head = head.getPredecessor();
        }

        if (tail != null) {
            distance += tail.getSegmentLength();
        }
        return distance;
    }

    // insert the newSegment linked list into the main path linked list
    private PathTuple insertSegment(PathTuple start, PathTuple end, PathTuple newSegment) {
        PathTuple theTail = newSegment;
        newSegment = reverseList(newSegment); // reverse the segment to be added, as it
        // is currently in the wrong order

        start.setPredecessor(newSegment.getPredecessor()); // start of segment links to
        // new segment's next link (head of new segment is currently the same as the start
        // head

        theTail.setPredecessor(end.getPredecessor()); // tail of new segment is linked
        // to next link of the head of the remaining existing path.
        end.setPredecessor(null);

        return start;
    }


}
