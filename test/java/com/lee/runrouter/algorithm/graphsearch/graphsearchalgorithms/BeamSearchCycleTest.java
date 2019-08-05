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
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicMain;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeamSearchCycleTest {
    ElementRepo repo;
    GraphSearch beamSearch;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    Heuristic featuresHeuristic;
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
        featuresHeuristic = new FeaturesHeuristicMain();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain();
        gradientCalculator = new SimpleGradientCalculator();

        beamSearch = new BeamSearchCycle(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }

    @Test(timeout = 3000)
    public void testMorrishRoad5k() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "morrish5k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void testMorrishRoad14k() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "morrish14k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void testMorrishRoad21k() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "morrish21k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        System.out.println(returnPath(res, ""));
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void testCraignairRoad5k()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "craignair5k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void testCraignairRoad14k()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "craignair14k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void testCraignairRoad21k()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "craignair21k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void TulseHillTest5KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "tulse5k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void TulseHillTest14KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "tulse14k";

        double length = calculateDistance(res);
        //serialize(res, name);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout = 3000)
    public void TulseHillTest21KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple res = beamSearch.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "tulse21k";

        double length = calculateDistance(res);
        //serialize(res, name);

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