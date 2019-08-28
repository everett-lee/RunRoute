package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BFSTest {
    ElementRepo repo;
    GraphSearch BFS;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    boolean saveRoutes;

    {
        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain();
        gradientCalculator = new SimpleGradientCalculator();

        BFS = new BFS(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        saveRoutes = false;
    }

    @Test(timeout = 30000)
    public void testMorrishRoad5k() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "morrish5k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }


        double sum = 0;
        for (int i = 0; i < 5; i++) {
            PathTuple testRes = BFS.searchGraph(repo.getOriginWay(), coords, 5000);
        }

        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();
            PathTuple testRes = BFS.searchGraph(repo.getOriginWay(), coords, 5000);
            long estimatedTime = System.nanoTime() - startTime;
            sum += estimatedTime;
        }

        System.out.println("TIME: " + sum / 50 / 1000000);

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 30000)
    public void testMorrishRoad14k() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "morrish14k";


        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 30000)
    public void testMorrishRoad21k() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "morrish21k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }


        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 3000)
    public void testCraignairRoad5k()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().get(5045576L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "craignair5k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 3000)
    public void testCraignairRoad14k()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().get(5045576L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "craignair14k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 3000)
    public void testCraignairRoad21k()  {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().get(5045576L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "craignair21k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 3000)
    public void TulseHillTest5KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().get(4004611L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 5000);
        String name = "tulse5k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }
        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 3000)
    public void TulseHillTest14KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().get(4004611L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 14000);
        String name = "tulse14k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 30000)
    public void TulseHillTest21KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().get(4004611L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
        String name = "tulse21k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
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