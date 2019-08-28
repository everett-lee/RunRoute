package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.*;
import com.lee.runrouter.algorithm.gradientcalculator.*;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.*;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.*;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.*;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class BFSConnectionPathTest {
    PathTuple morrish5k;
    PathTuple morrish14k;
    PathTuple morrish21k;
    PathTuple tulse14k;
    PathTuple craignair14k;
    ILSGraphSearch connectPath;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    ElementRepo repo;

    {
        morrish5k = getMorrish5k();
        tulse14k = getTulse14k();
        craignair14k = getCraignair14k();
        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DirectDistanceHeuristic(distanceCalculator);

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
    public void testMorrishShortSegment() {
        PathTuple start = reverseList(morrish5k);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 7);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);

        PathTuple resFinal = getEndTuple(res);

        System.out.println(returnPath(res, ""));

        assertEquals(start.getCurrentNode(), resFinal.getCurrentNode());
        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
    }

    @Test
    public void testMorrishSingleSegment() {
        PathTuple start = reverseList(morrish5k);

        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 1);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);

        System.out.println(calculateScore(res));

        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
    }


    @Test
    public void testMorrishLongSegment() {
        PathTuple start = reverseList(morrish5k);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 4);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);

        System.out.println(calculateScore(res));

        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
    }



    @Test
    public void testMorrishLongSegmentII() {
        PathTuple start = reverseList(morrish5k);


        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 16);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);

        System.out.println(calculateScore(res));

        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
    }

    @Test
    public void testCraigShortSegment() {
        PathTuple start = reverseList(craignair14k);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 6);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);


        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
    }

    @Test
    public void testCraignairLongerSegment() {
        PathTuple start = reverseList(craignair14k);


        start = getStartPathSegment(start, 5);
        PathTuple end = getEndPathSegment(start, 16);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);

        System.out.println(calculateScore(res));
        System.out.println(returnPath(res, ""));

        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
    }


    @Test
    public void testTulseLongSegmentII() {
        PathTuple start = reverseList(tulse14k);


        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 20);

        PathTuple res = connectPath.connectPath(start,
                end, 5000, 0);

        System.out.println(calculateScore(res));

        assertEquals(start.getCurrentNode().getId(), getEndTuple(res).getCurrentNode().getId());
        assertEquals(end.getCurrentNode().getId(), res.getCurrentNode().getId());
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

        if (endNode.getCurrentNode() == null) {
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