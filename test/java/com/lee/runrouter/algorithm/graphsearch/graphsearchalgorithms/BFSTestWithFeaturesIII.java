package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.EuclideanCalculator;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.assertTrue;

public class BFSTestWithFeaturesIII {
    ElementRepo repoCEN;
    GraphSearch BFS;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;

    {
        repoCEN = getRepoCEN();
    }

    {
        distanceCalculator = new EuclideanCalculator();
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

        BFS = new BFS(repoCEN, distanceHeuristic,
                 featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }

    @Test(timeout = 5000)
    public void test2kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 2000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test2kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 2000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test4kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 4000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test4kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 4000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test6kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 6000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test6kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 6000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test8kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 8000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test8kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 8000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test10kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 10000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test10kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 10000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test12kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 12000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test12kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 12000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


    @Test(timeout = 5000)
    public void test14kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 14000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test14kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 14000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


    @Test(timeout = 5000)
    public void test16kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 16000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test16kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 16000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test18kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 18000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test18kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 18000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test20kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 20000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test20kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 20000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test21kUphill() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 21000;
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void test21kFlat() {
        double[] coords = {51.522011, -0.130900};
        repoCEN.setOriginWay(repoCEN.getWayRepo().get(505739792L));
        double target = 21000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFS.searchGraph(repoCEN.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


}