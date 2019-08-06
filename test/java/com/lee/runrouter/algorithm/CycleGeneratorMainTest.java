package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.distanceCalculator.*;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.routegenerator.cyclegenerator.*;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BeamSearch;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BeamSearchReturnPath;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import static com.lee.runrouter.testhelpers.TestHelpers.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CycleGeneratorMainTest {
    ElementRepo repo;
    GraphSearch beamSearch;
    GraphSearch returnPath;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    CycleGenerator cycleGenerator;

    {
        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList());
        List<String> preferredHighways = new ArrayList<>(Arrays.asList());
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
        featuresHeuristic.setPreferredHighways(preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(false);


        beamSearch = new BeamSearch(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        returnPath = new BeamSearchReturnPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        cycleGenerator = new CycleGeneratorMain(beamSearch, returnPath, repo);
    }

    @Test(timeout=2000)
    public void testMorrishRoad5k() throws PathNotGeneratedException {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = cycleGenerator.generateCycle(coords, 5000);
        String name = "morrish5k";
        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testMorrishRoad14k() throws PathNotGeneratedException {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = cycleGenerator.generateCycle(coords, 14000);
        String name = "morrish14k";
        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testMorrishRoad21k() throws PathNotGeneratedException {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = cycleGenerator.generateCycle(coords, 21000);
        String name = "morrish21k";

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }


    @Test(timeout=2000)
    public void testMorrishRoadProblemOne() throws PathNotGeneratedException {
        double[] coords = {51.446461, -0.125472};
        String name = "morrishProblem1";
        PathTuple res = cycleGenerator.generateCycle(coords,        5500);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testMorrishRoadProblemTwo() throws PathNotGeneratedException {
        double[] coords = {51.447387,-0.126467};
        String name = "morrishProblem2";
        PathTuple res = cycleGenerator.generateCycle(coords, 5000);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testCraignairRoad5k() throws PathNotGeneratedException {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        String name = "craignair5k";
        PathTuple res = cycleGenerator.generateCycle(coords, 5000);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testCraignairRoad14k() throws PathNotGeneratedException {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        String name = "craignair14k";
        PathTuple res = cycleGenerator.generateCycle(coords, 14000);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testCraignairRoad21k() throws PathNotGeneratedException {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        String name = "craignair21k";
        PathTuple res = cycleGenerator.generateCycle(coords, 21000);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testTulseHillRoundTrip5KM() throws PathNotGeneratedException {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        String name = "tulse5k";
        PathTuple res = cycleGenerator.generateCycle(coords, 5000);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());

    }

    @Test(timeout=2000)
    public void testTulseHillRoundTrip14KM() throws PathNotGeneratedException {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        String name = "tulse14k";
        PathTuple res = cycleGenerator.generateCycle(coords, 14000);

        double length = calculateDistance(res);
        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    @Test(timeout=2000)
    public void testTulseHillRoundTrip21KM() throws PathNotGeneratedException {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        String name = "tulse21k";
        PathTuple res = cycleGenerator.generateCycle(coords, 21000);

        double length = calculateDistance(res);

        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }

    private double calculateDistance(PathTuple root) {
        double distance = 0;

        while (root != null) {
            distance += root.getSegmentLength();
            root = root.getPredecessor();
        }

        return distance;
    }

    static void serialize(PathTuple head, String routeName) {
        try {
            System.out.println("Starting... ");
            String fileName = String.format("/home/lee/project/app/runrouter/src/%s.ser", routeName);
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