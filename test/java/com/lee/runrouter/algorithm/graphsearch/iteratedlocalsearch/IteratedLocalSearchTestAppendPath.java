package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFSConnectionPath;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IteratedLocalSearchTestAppendPath {
    PathTuple morrishWay5k;
    PathTuple tulse14k;
    ILSGraphSearch connectPath;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    ElevationHeuristic elevationHeuristic;
    GradientCalculator gradientCalculator;
    ElementRepo repo;

    {
        morrishWay5k = getMorrish5k();
        tulse14k = getTulse14k();
        repo = getRepoSW();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        featuresHeuristic.setPreferredHighways(preferredHighways);
        featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);


        connectPath = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }

    @Test
    public void testInsertSingleNode() {
        PathTuple head = reverseList(morrishWay5k);


        PathTuple originalHead = head;
        PathTuple originalTail = getTail(head);

        PathTuple start = getStartPathSegment(head, 1);
        PathTuple end = getEndPathSegment(start, 1);

        PathTuple newSegment = connectPath.connectPath(start,
                end, 2000, end.getTotalLength());

        PathTuple newTail = getTail(head);

        assertEquals(originalHead, head);
        assertEquals(originalTail, newTail);
    }

    @Test
    public void testInsertSeveralNodes() {
        PathTuple head = reverseList(morrishWay5k);

        PathTuple originalHead = head;
        PathTuple originalTail = getTail(head);

        PathTuple start = getStartPathSegment(head, 4);
        PathTuple end = getEndPathSegment(start, 9);

        PathTuple newSegment = connectPath.connectPath(start,
                end, 2000, end.getTotalLength());

        returnPath(newSegment, "");

        insertSegment(start, end,  newSegment);
        PathTuple newTail = getTail(head);

        assertEquals(originalHead, head);
        assertEquals(originalTail, newTail);
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

    private PathTuple getTail(PathTuple head) {
        while (head.getPredecessor() != null) {
            head = head.getPredecessor();
        }
        return head;
    }

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
