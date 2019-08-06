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
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeamSearchCycleTestWithFeatures {
    ElementRepo repo;
    GraphSearch beamSearch;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;

    {
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
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);
        gradientCalculator = new SimpleGradientCalculator();

        beamSearch = new BeamSearchCycle(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }

    @Test(timeout = 5000)
    public void testMorrishRoad5kUphill() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "morrish5kWithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testMorrishRoad5kFlat() {
        double[] coords = {51.446810, -0.125484};
        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "morrish5kWithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }


    @Test(timeout = 5000)
    public void testMorrishRoad14kUphill() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "morrish14kWithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testMorrishRoad14kFlat() {
        double[] coords = {51.446810, -0.125484};
        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "morrish14kWithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }


    @Test(timeout = 5000)
    public void testMorrishRoad21kUphill() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 22000);
        String name = "morrish21kWithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testMorrishRoad21kFlat() {
        double[] coords = {51.446810, -0.125484};
        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "morrish21kWithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testCraignairRoad5kUphill()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "craignair5kwithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testCraignairRoad5kFlat()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "craignair5kwithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testCraignairRoad14kUphill()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "craignair14kwithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testCraignairRoad14kFlat()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "craignair14kwithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testCraignairRoad21kUphill()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "craignair21kwithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void testCraignairRoad21kFlat()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 22000);
        String name = "craignair21kwithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void TulseHillTest5kmUphill() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "tulse5kWithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void TulseHillTest5kmFlat() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "tulse5kWithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }


    @Test(timeout = 5000)
    public void TulseHillTest14kmUphill() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "tulse14kWithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void TulseHillTest14kmFlat() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "tulse14kWithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void TulseHillTest21kmUphill() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "tulse21kWithFeaturesUphill";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 5000)
    public void TulseHillTest21kmFlat() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        elevationHeuristic.setOptions(false);
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "tulse21kWithFeaturesFlat";

        double length = calculateDistance(res);
        serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    static void serialize(PathTuple head, String routeName) {
        try {
            System.out.println("Starting... ");
            String fileName = String.format("/home/lee/project/app/runrouter/src/savedRoutes/%s.ser", routeName);
            FileOutputStream fileOut =
                    new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(head);
            out.close();
            fileOut.close();
            System.out.printf(fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

}