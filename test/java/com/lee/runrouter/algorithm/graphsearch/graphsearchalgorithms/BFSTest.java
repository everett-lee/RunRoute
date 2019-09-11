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
        repo = getRepoSW();
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


//        double sum = 0;
//        double max = 0;
//        int fails = 0;
//        double min = Integer.MAX_VALUE;
//        for (int i = 0; i < 5; i++) {
//            PathTuple testRes = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
//        }
//
//        for (int i = 0; i < 1000; i++) {
//            System.out.println(i);
//            long startTime = System.nanoTime();
//            PathTuple testRes = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
//            long estimatedTime = System.nanoTime() - startTime;
//            sum += estimatedTime;
//            max = Math.max(estimatedTime, max);
//            min = Math.min(estimatedTime, min);
//            if (testRes.getTotalLength() == -1) {
//                fails++;
//            }
//        }
//
//        System.out.println("TIME: " + sum / 1000 / 1000000);
//        System.out.println("MAX: " + max / 1000000);
//        System.out.println("MIN: " + min / 1000000);
//        System.out.println("FAILS " + fails);

    @Test
    public void testMorrishRoad5k() {
        double[] coords = {51.446810, -0.125484};
        double target = 5000;
        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "morrish5k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void testMorrishRoad14k() {
        double[] coords = {51.446810, -0.125484};
        double target = 14000;
        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "morrish14k";


        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void testMorrishRoad21k() {
        double[] coords = {51.446810, -0.125484};
        double target = 21000;
        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "morrish21k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void testCraignairRoad5k()  {
        double[] coords = {51.448321, -0.114648};
        double target = 5000;
        Way origin = repo.getWayRepo().get(5045576L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "craignair5k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void testCraignairRoad14k()  {
        double[] coords = {51.448321, -0.114648};
        double target = 14000;
        Way origin = repo.getWayRepo().get(5045576L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "craignair14k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void testCraignairRoad21k()  {
        double[] coords = {51.448321, -0.114648};
        double target = 21000;
        Way origin = repo.getWayRepo().get(5045576L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "craignair21k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void TulseHillTest5KM() {
        double[] coords = {51.441109, -0.106974};
        double target = 5000;
        Way origin = repo.getWayRepo().get(4004611L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "tulse5k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }
        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void TulseHillTest14KM() {
        double[] coords = {51.441109, -0.106974};
        double target = 14000;
        Way origin = repo.getWayRepo().get(4004611L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "tulse14k";

        double length = calculateDistance(res);

        if (saveRoutes) {
            serialize(res, name);
        }

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test
    public void TulseHillTest21KM() {
        double[] coords = {51.441109, -0.106974};
        double target = 21000;
        Way origin = repo.getWayRepo().get(4004611L);
        repo.setOriginWay(origin);

        PathTuple res = BFS.searchGraph(repo.getOriginWay(), coords, target);
        String name = "tulse21k";

        double length = calculateDistance(res);



        double sum = 0;
        double max = 0;
        int fails = 0;
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            PathTuple testRes = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
        }

        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            long startTime = System.nanoTime();
            PathTuple testRes = BFS.searchGraph(repo.getOriginWay(), coords, 21000);
            long estimatedTime = System.nanoTime() - startTime;
            sum += estimatedTime;
            max = Math.max(estimatedTime, max);
            min = Math.min(estimatedTime, min);
            if (testRes.getTotalLength() == -1) {
                fails++;
            }
        }

        System.out.println("TIME: " + sum / 1000 / 1000000);
        System.out.println("MAX: " + max / 1000000);
        System.out.println("MIN: " + min / 1000000);
        System.out.println("FAILS " + fails);

        assertTrue(length > 5);
        assertTrue(res.getTotalLength() > target * 0.925);
        assertTrue(res.getTotalLength() < target * 1.05);
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