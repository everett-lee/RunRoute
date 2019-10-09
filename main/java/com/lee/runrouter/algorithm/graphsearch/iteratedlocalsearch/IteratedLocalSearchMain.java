package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;

@Component
@Qualifier("IteratedLocalSearchMain")
public class IteratedLocalSearchMain implements IteratedLocalSearch {
    private ILSGraphSearch graphSearch;
    private final long TIME_LIMIT = 1500;
    private int iterations;
    private int improvements;
    private int noImprovement; // number of iterations that have not yielded
    // an improved score
    private HashSet<Long> includedWays; // Ways included in the current Path

    private final int MAX_NO_IMPROVEMENT = 50; // the maximum number of iterations without
    // an improvement before the algorithm stops

    @Autowired
    public IteratedLocalSearchMain(@Qualifier("BFSConnectionPath") ILSGraphSearch graphSearch) {
        this.graphSearch = graphSearch;
    }

    /**
     * Iterates over a linked list of PathTuples, representing route generated
     * by the greedy best-first search (BFS) algorithm, and removes segments of
     * increasing length. A BFS is then used to 'close the gap' between the start
     * and end point of the removed segment. If the resulting path segment has a
     * higher score, it is used as a replacement for the removed segment.
     *
     * @param head the head PathTuple of the input route
     * @param distanceToAdd available excess distance to add to the route. This
     *                      is the distance between the target and actual length
     *                      of the route.
     * @return A PathTuple that is the head of a new linked list with the new
     * path segments added
     */
    @Override
    public PathTuple iterate(PathTuple head, double distanceToAdd) {
        setIterations(0);
        setImprovements(0);
        setNoImprovement(0);
        populateAndSetIncludedWays(head);

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        double targetDistance = head.getTotalLength() + distanceToAdd; // target is current route length
        // plus the remaining available distance
        double availableDistance = distanceToAdd; // distance left available to add to route

        // begin by reversing the incoming path
        head = reverseList(head);

        int a = 1; // the starting node, indexed from 1
        int r = 2; // number of edges to remove

        while (elapsedTime <= TIME_LIMIT && (this.getNoImprovement() < MAX_NO_IMPROVEMENT)) {
            elapsedTime = (new Date()).getTime() - startTime;

            // get the number of nodes in the the path
            int pathSize = getPathSize(head);
            // reset if r if it will remove the penultimate edge
            if (r > pathSize - 3) {
                r = 2;
            }

            // increment r and reset a if a + r results in removal
            // of the penultimate edge
            if (a + r > pathSize - 2) {
                r = r + 1;
                a = 1;
            }

            PathTuple start = getStartPathNode(head, a); // start Node of the removed segment
            PathTuple end = getEndPathNode(start, r); // end Node of the removed segment

            // calculate the length in metres of the segment to be removed, and
            // add to available distance
            double existingSegmentDistance = calculateDistance(start, end); // does not include
            // start, includes end
            availableDistance += existingSegmentDistance;

            PathTuple newSegment = null;
            if (availableDistance > 0) {
                // generate the new segment
                newSegment = graphSearch.connectPath(start, end, availableDistance,
                        end.getTotalLength());
            }

            setIterations(getIterations() + 1);

            newSegment = reverseList(newSegment); // reverse the segment to be added, as it
            // is currently in the wrong order

            double oldSegmentScore = calculateScore(start, end);
            double newSegmentScore = calculateScore(newSegment, null);

            // if old segment had a better score, or a new segment was not found
            if (oldSegmentScore >= newSegmentScore || newSegment == null
                    || newSegment.getSegmentLength() == -1) {
                a++;

                // subtract the length in metres of the segment length as it was not removed
                availableDistance -= existingSegmentDistance;

                this.setNoImprovement(this.getNoImprovement() + 1);

            // new segment score is higher, so replace old path segment with the new one
            } else {
                setImprovements(getImprovements() + 1);
                insertSegment(start, end, newSegment);

                // update current node distances and target distance to reflect added segment
                double newDistance = updateDistancesAndIncludedWays(head);
                availableDistance = targetDistance - newDistance;

                a = 1;
                r = 2;
            }
        }
        // clear visited Nodes for the next round
        graphSearch.resetVisitedNodes();
        return head;
    }

    // creates a set of visited Ways to pass to the graph search algorithm
    private void populateAndSetIncludedWays(PathTuple head) {
        includedWays = new HashSet<>();
        while (head != null) {
            includedWays.add(head.getCurrentWay().getId());
            head = head.getPredecessor();
        }
        graphSearch.setIncludedWays(includedWays);
    }

    // update the route distance and included Ways to reflect
    // newly added path segment
    private double updateDistancesAndIncludedWays(PathTuple head) {
        double runningDistance = 0;
        double finalDistance = 0;
        includedWays.clear();

        while (head != null) {
            head.setTotalLength(runningDistance);
            runningDistance += head.getSegmentLength();
            includedWays.add(head.getCurrentWay().getId());

            // get the new final distance
            if (head.getPredecessor() == null) {
                finalDistance = head.getTotalLength();
            }

            head = head.getPredecessor();
        }
        return finalDistance;
    }

    // reverse the linked list of tuples so that that they are in
    // the order of arrival
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

    // get a reference to the node a places into the linked list of PathTuples
    // this is the start node of the removed segment
    private PathTuple getStartPathNode(PathTuple head, int a) {
        int i = 0;
        while (i < a - 1) {
            head = head.getPredecessor();
            i++;
        }
        return head;
    }

    // get a reference to the node r places from the start of the removed
    // segment
    private PathTuple getEndPathNode(PathTuple endNode, int r) {
        int i = 0;

        if (endNode.getCurrentNode() == null) {
            return endNode;
        }

        while (i < r) {
            endNode = endNode.getPredecessor();
            i++;
        }
        return endNode;
    }

    // get the length of the path in terms of number of nodes
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
            score += head.getSegmentScore().getHeuristicScore();
            head = head.getPredecessor();
        }


        if (tail != null) {
            score += head.getSegmentScore().getHeuristicScore();
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

        // get a reference to the tail node
        while (theTail.getPredecessor() != null) {
            theTail = theTail.getPredecessor();
        }

        start.setPredecessor(newSegment.getPredecessor()); // start of segment links to
        // new segment's next link (head of new segment is currently the same as the start
        // head

        theTail.setPredecessor(end.getPredecessor()); // tail of new segment is linked
        // to next link of the head of the remaining existing path.
        end.setPredecessor(null);
        return start;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getImprovements() {
        return improvements;
    }

    public void setImprovements(int improvements) {
        this.improvements = improvements;
    }

    public int getNoImprovement() {
        return noImprovement;
    }

    public void setNoImprovement(int noImprovement) {
        this.noImprovement = noImprovement;
    }
}
