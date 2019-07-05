package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class BeamSearchConnectionPathTest {
    PathTuple morrishWayShort;
    PathTuple tulseHillLonger;
    ILSGraphSearch connectPath;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    ElevationHeuristic elevationHeuristic;
    ElementRepo repo;

    {
        morrishWayShort = getMorrishShort();
        tulseHillLonger = getTulseLong();
        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain(true);


        connectPath = new BeamSearchConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);
    }


    @Test
    public void testMorrishShortSegment() {
        PathTuple start = reverseList(morrishWayShort);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 7);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 2000);

        PathTuple resFinal = getEndTuple(res);

        //assertEquals(end.getPreviousNode(), res.getPreviousNode());
        assertEquals(start.getPreviousNode(), resFinal.getPreviousNode());
        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }

    @Test
    public void testMorrishSingleSegment() {
        PathTuple start = reverseList(morrishWayShort);

        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 1);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 2000);


        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }


    @Test
    public void testMorrishLongSegment() {
        PathTuple start = reverseList(morrishWayShort);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 4);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 2000);


        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());

    }



    @Test
    public void testMorrishLongSegmentII() {
        PathTuple start = reverseList(morrishWayShort);

        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 16);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 2000);


        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());

    }

    @Test
    public void testTulseLongSegmentII() {
        PathTuple start = reverseList(tulseHillLonger);


        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 80);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 2000);

        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
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

    static PathTuple getEndTuple(PathTuple head) {
        while (head.getPredecessor() != null) {
            head = head.getPredecessor();
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
}