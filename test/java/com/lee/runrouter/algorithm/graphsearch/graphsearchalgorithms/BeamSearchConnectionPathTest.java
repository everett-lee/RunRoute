//package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;
//
//import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
//import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
//import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
//import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
//import com.lee.runrouter.algorithm.heuristic.*;
//import com.lee.runrouter.algorithm.pathnode.PathTuple;
//import com.lee.runrouter.graph.elementrepo.ElementRepo;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.lee.runrouter.testhelpers.TestHelpers.*;
//
//public class BeamSearchConnectionPathTest {
//    PathTuple morrishWayShort;
//    PathTuple tulseHillLonger;
//    ILSGraphSearch connectPath;
//    DistanceCalculator distanceCalculator;
//    Heuristic distanceHeuristic;
//    Heuristic featuresHeuristic;
//    EdgeDistanceCalculator edgeDistanceCalculator;
//    ElevationHeuristic elevationHeuristic;
//    ElementRepo repo;
//
//    {
//        morrishWayShort = getMorrishShort();
//        tulseHillLonger = getTulseLong();
//        repo = getRepo();
//    }
//
//    @Before
//    public void setUp() {
//        distanceCalculator = new HaversineCalculator();
//        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);
//
//        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
//                "DIRT", "GRAVEL"));
//        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
//                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
//        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
//        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
//        elevationHeuristic = new ElevationHeuristicMain(true);
//
//
//        connectPath = new BeamSearchConnectionPath(repo, distanceHeuristic,
//                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);
//    }
//
//
//
//    @Test
//    public void testMorrishShortSegment() {
//        PathTuple start = reverseList(morrishWayShort);
//
//        start = getStartPathSegment(start, 1);
//        PathTuple end = getEndPathSegment(start, 1);
//
//        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
//        connectPath.setCurrentDistance(3000);
//        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());
//
//        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 2000);
//
//        assertEquals(114280162, getEndTuple(reverseList(res)).getPreviousNode().getId());
//    }
//
//    @Test
//    public void testMorrishLongSegment() {
//        PathTuple start = reverseList(morrishWayShort);
//
//        start = getStartPathSegment(start, 2);
//        PathTuple end = getEndPathSegment(start, 3);
//
//        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
//        connectPath.setCurrentDistance(3000);
//        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());
//
//        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 2000);
//
//        assertEquals(290017242, getEndTuple(reverseList(res)).getPreviousNode().getId());
//    }
//
//
//    @Test
//    public void testMorrishLongSegmentTwo() {
//        PathTuple start = reverseList(morrishWayShort);
//
//        start = getStartPathSegment(start, 5);
//        PathTuple end = getEndPathSegment(start, 5);
//
//        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
//        connectPath.setCurrentDistance(3000);
//        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());
//
//        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 2000);
//
//        assertEquals(1547012858, getEndTuple(reverseList(res)).getPreviousNode().getId());
//    }
//
//
//
//    @Test
//    public void testTulsehShortSegment() {
//        PathTuple start = tulseHillLonger;
//
//        start = getStartPathSegment(start, 4);
//        PathTuple end = getEndPathSegment(start, 6);
//
//        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
//        connectPath.setCurrentDistance(3000);
//        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());
//
//        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 2000);
//
//        assertEquals(1547012843, getEndTuple(reverseList(res)).getPreviousNode().getId());
//    }
//
//    @Test
//    public void testTulseLongSegment() {
//        PathTuple start = tulseHillLonger;
//
//        start = getStartPathSegment(start, 2);
//        PathTuple end = getEndPathSegment(start, 10);
//
//        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
//        connectPath.setCurrentDistance(3000);
//        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());
//
//        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 2000);
//
//        assertEquals(432952, getEndTuple(reverseList(res)).getPreviousNode().getId());
//    }
//
//
//    private PathTuple getStartPathSegment(PathTuple head, int a) {
//        int i = 0;
//        while (i < a-1) {
//            head = head.getPredecessor();
//            i++;
//        }
//        return head;
//    }
//
//    private PathTuple getEndPathSegment(PathTuple endNode, int r) {
//        int i = 0;
//
//        while (i < r) {
//            endNode = endNode.getPredecessor();
//            i++;
//        }
//        return endNode;
//    }
//
//    static PathTuple getEndTuple(PathTuple head) {
//        while (head.getPredecessor() != null) {
//            head = head.getPredecessor();
//        }
//        return head;
//    }
//
//    private PathTuple reverseList(PathTuple head) {
//        PathTuple prev = null;
//        PathTuple current = head;
//        PathTuple next = null;
//
//        while (current != null) {
//            next = current.getPredecessor();
//            current.setPredecessor(prev);
//            prev = current;
//            current = next;
//        }
//        return prev;
//    }
//}