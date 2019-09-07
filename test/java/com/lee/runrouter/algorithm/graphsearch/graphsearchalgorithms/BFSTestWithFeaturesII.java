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
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BFSTestWithFeaturesII {
    ElementRepo repoLAW;
    GraphSearch BFSLaw;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;

    {
        repoLAW = getRepoLAW();
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

        BFSLaw = new BFS(repoLAW, distanceHeuristic,
                 featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

    }

    @Test(timeout = 5000)
    public void testLaw5kUphill() {
        double[] coords = {51.938148, 1.047673};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(26437078L));
        double target = 5000;
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testLaw5kFlat() {
        double[] coords = {51.938148, 1.047673};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(26437078L));
        double target = 5000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


    @Test(timeout = 5000)
    public void testLaw14kUphill() {
        double[] coords = {51.938148, 1.047673};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(26437078L));
        double target = 14000;
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testLaw14kFlat() {
        double[] coords = {51.938148, 1.047673};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(26437078L));
        double target = 14000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


    @Test(timeout = 5000)
    public void testLaw21kUphill() {
        double[] coords = {51.938148, 1.047673};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(26437078L));
        double target = 21000;
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testLaw21kFlat() {
        double[] coords = {51.938148, 1.047673};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(26437078L));
        double target = 21000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testMan5kUphill() {
        double[] coords = {51.948196, 1.050655};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734327L));
        double target = 5000;
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testMan5kFlat() {
        double[] coords = {51.948196, 1.050655};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734327L));
        double target = 5000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testMan14kUphill() {
        double[] coords = {51.948196, 1.050655};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734327L));
        double target = 14000;
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testMan14kFlat() {
        double[] coords = {51.948196, 1.050655};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734327L));
        double target = 14000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


    @Test(timeout = 5000)
    public void testMan21kUphill() {
        double[] coords = {51.948196, 1.050655};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734327L));
        double target = 21000;
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }

    @Test(timeout = 5000)
    public void testMan21kFlat() {
        double[] coords = {51.948196, 1.050655};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734327L));
        double target = 21000;
        elevationHeuristic.setOptions(false);
        PathTuple res = BFSLaw.searchGraph(repoLAW.getOriginWay(), coords, target);

        double length = calculateDistance(res);

        assertTrue(length >= 5);
        assertTrue(res.getTotalLength() >=  target * 0.95);
        assertTrue(res.getTotalLength() <= target * 1.05);
        assertTrue(res.getCurrentNode().getId() == getTail(res).getCurrentNode().getId());
    }


}