package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.distanceCalculator.*;
import com.lee.runrouter.algorithm.graphsearch.*;
import com.lee.runrouter.algorithm.graphsearch.cyclegenerator.*;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import static com.lee.runrouter.testhelpers.TestHelpers.*;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CycleGeneratorMainTest {
    ElementRepo repo;
    GraphSearch beamSearch;
    GraphSearch returnPath;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    ElevationHeuristic elevationHeuristic;
    CycleGenerator ils;

    {
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


        beamSearch = new BeamSearch(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic);

        returnPath = new BeamSearchReturnPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);

        ils = new CycleGeneratorMain(beamSearch, returnPath, repo);
    }

    @Test(timeout=5000)
    public void testMorrishRoadShortRoundTrip() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = ils.generateCycle(coords, 5);

        String str = "";
        str = returnPath(res, str);
        System.out.println(str);

   }

    @Test(timeout=5000)
    public void testMorrishRoadLongRoundTrip() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = ils.generateCycle(coords, 10);

        String str = "";
        str = returnPath(res, str);
        System.out.println(str);
    }


    @Test(timeout=5000)
    public void testCraignairRoadShortRoundTrip() {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple res = ils.generateCycle(coords, 5);

        String str = "";
        str = returnPath(res, str);
        System.out.println(str);

    }

    @Test(timeout=5000)
    public void testCraignairRoadLongerRoundTrip() {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple res = ils.generateCycle(coords, 10);

        String str = "";
        str = returnPath(res, str);
        System.out.println(str);
    }


    @Test(timeout=5000)
    public void testTulseHillRoundTrip10KM() {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple res = ils.generateCycle(coords, 10);

        String str = "";
        str = returnPath(res, str);
        System.out.println(str);
    }
}