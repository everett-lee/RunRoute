package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.distanceCalculator.*;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.cyclegenerator.*;
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
    Heuristic distanceHeuristic;
    Heuristic featuresHeuristic;
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
        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain(true);


        beamSearch = new BeamSearch(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        returnPath = new BeamSearchReturnPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        cycleGenerator = new CycleGeneratorMain(beamSearch, returnPath, repo);
    }

    @Test(timeout=5000)
    public void testMorrishRoadShortRoundTrip() throws Exception {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = cycleGenerator.generateCycle(coords, 5000);

        double length = calculateDistance(res);
        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
   }

    @Test(timeout=5000)
    public void testMorrishRoadLongRoundTrip() throws Exception {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = cycleGenerator.generateCycle(coords, 10000);

        double length = calculateDistance(res);
        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());
    }


    @Test(timeout=5000)
    public void testCraignairRoadShortRoundTrip() throws Exception {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple res = cycleGenerator.generateCycle(coords, 5000);

        double length = calculateDistance(res);
        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());

    }

    @Test(timeout=5000)
    public void testCraignairRoadLongerRoundTrip() throws Exception {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple res = cycleGenerator.generateCycle(coords, 10000);


        double length = calculateDistance(res);
        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());


    }


    @Test(timeout=5000)
    public void testTulseHillRoundTrip10KM() throws Exception {
        double[] coords = {51.441109, -0.106974};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple res = cycleGenerator.generateCycle(coords, 10000);

        double length = calculateDistance(res);
        assertTrue(calculateScore(res) > 0);
        assertEquals(length, res.getTotalLength(), 0.01);
        assertTrue(res.getPreviousNode().getId() == getTail(res).getPreviousNode().getId());


        System.out.println(returnPath(res,""));

    }

    private double calculateDistance(PathTuple root) {
        double distance = 0;

        while (root != null) {
            distance += root.getSegmentLength();
            root = root.getPredecessor();
        }

        return distance;
    }

    static void serialize(PathTuple head) {
        try {
            System.out.println("Starting... ");
            FileOutputStream fileOut =
                    new FileOutputStream("/home/lee/project/app/runrouter/src/tulsehilllong.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(head);
            out.close();
            fileOut.close();
            System.out.printf("/home/lee/project/app/runrouter/src/tulsehilllong.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }

    }
}