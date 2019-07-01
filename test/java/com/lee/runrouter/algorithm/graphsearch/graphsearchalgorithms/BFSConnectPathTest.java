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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

import static org.junit.Assert.*;

public class BFSConnectPathTest {
    PathTuple morrishWayShort;
    PathTuple tulseHillLonger;
    ConnectPathGraphSearch connectPath;
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


        connectPath = new BFSConnectPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);
    }



    @Test
    public void testMorrishShortSegment() {
        PathTuple start = morrishWayShort;

        for (int i = 0; i < 4; i++) {
            start = start.getPredecessor();
        }

        PathTuple end = start;

        for (int i = 0; i < 3; i++) {
            end = end.getPredecessor();
        }

        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
        connectPath.setCurrentDistance(3000);
        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());


        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 20000);

        String out = "";
        out = returnPath(res, out);
        System.out.println(out);
    }

    @Test
    public void testMorrishLongSegment() {
        PathTuple start = morrishWayShort;

        for (int i = 0; i < 4; i++) {
            start = start.getPredecessor();
        }

        PathTuple end = start;

        for (int i = 0; i < 25; i++) {
            end = end.getPredecessor();
        }

        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
        connectPath.setCurrentDistance(3000);
        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());

        System.out.println(end.getCurrentWay());


        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 20000);

        String out = "";
        out = returnPath(res, out);
        System.out.println(out);
    }


    @Test
    public void testTulsehShortSegment() {
        PathTuple start = tulseHillLonger;

        for (int i = 0; i < 4; i++) {
            start = start.getPredecessor();
        }

        PathTuple end = start;

        for (int i = 0; i < 3; i++) {
            end = end.getPredecessor();
        }

        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
        connectPath.setCurrentDistance(3000);
        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());


        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 20000);

        String out = "";
        out = returnPath(res, out);
        System.out.println(out);
    }

    @Test
    public void testTulseLongSegment() {
        PathTuple start = tulseHillLonger;

        for (int i = 0; i < 4; i++) {
            start = start.getPredecessor();
        }

        PathTuple end = start;

        for (int i = 0; i < 25; i++) {
            end = end.getPredecessor();
        }

        double[] startCoords = {start.getPreviousNode().getLat(), start.getPreviousNode().getLon()};
        connectPath.setCurrentDistance(3000);
        connectPath.setTarget(end.getPreviousNode(), end.getCurrentWay());


        PathTuple res = connectPath.searchGraph(start.getCurrentWay(), startCoords, 20000);

        String out = "";
        out = returnPath(res, out);
        System.out.println(out);
    }

}