package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class BeamSearchConnectionPathTest {
    PathTuple morrish5k;
    PathTuple tulseHill14k;
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
        tulseHill14k = getTulse14k();
        craignair14k = getCraignair14k();
        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET", "PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        featuresHeuristic.setPreferredHighways(preferredHighways);
        featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);


        connectPath = new BeamSearchConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }


    @Test
    // route A
    public void testMorrishShortSegment() {
        PathTuple start = reverseList(morrish5k);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 7);


        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 5000, start.getTotalLength(),
                0);

        PathTuple resFinal = getEndTuple(res);

        System.out.println(calculateScore(res));

        assertEquals(start.getPreviousNode(), resFinal.getPreviousNode());
        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }

    @Test
    public void testMorrishSingleSegment() {
        PathTuple start = reverseList(morrish5k);

        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 3);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 5000, start.getTotalLength(),
                0);

        System.out.println(calculateScore(res));

        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }


    @Test
    public void testMorrishLongSegment() {
        PathTuple start = reverseList(morrish5k);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 4);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 5000, start.getTotalLength(),
                0);


        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }


    @Test
    public void testMorrishLongSegmentII() {
        PathTuple start = reverseList(morrish5k);


        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 16);


        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 5000, start.getTotalLength(),
                0);


        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }

    @Test
    public void testCraigShortSegment() {
        PathTuple start = reverseList(craignair14k);

        start = getStartPathSegment(start, 3);
        PathTuple end = getEndPathSegment(start, 6);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 1000, start.getTotalLength(),
                2000);


        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }

    @Test
    // Route B
    public void testCraignairLongerSegment() {
        PathTuple start = reverseList(craignair14k);

        start = getStartPathSegment(start, 5);
        PathTuple end = getEndPathSegment(start, 16);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 2000, start.getTotalLength(),
                2000);

        assertEquals(start.getPreviousNode().getId(), getEndTuple(res).getPreviousNode().getId());
        assertEquals(end.getPreviousNode().getId(), res.getPreviousNode().getId());
    }


    @Test
    public void testTulseLongSegmentII() {
        PathTuple start = reverseList(tulseHill14k);


        start = getStartPathSegment(start, 1);
        PathTuple end = getEndPathSegment(start, 20);

        PathTuple res = connectPath.connectPath(start.getPreviousNode(), start.getCurrentWay(),
                end.getPreviousNode(), end.getCurrentWay(), 5000, start.getTotalLength(),
                0);

        System.out.println(calculateScore(res));

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