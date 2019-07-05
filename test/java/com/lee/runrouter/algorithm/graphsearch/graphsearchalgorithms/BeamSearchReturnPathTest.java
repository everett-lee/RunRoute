package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class BeamSearchReturnPathTest {
    ElementRepo repo;
    GraphSearch returnPath;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
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
        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain(true);


        returnPath = new BeamSearchReturnPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }

    @Test(timeout=3000)
    public void testMorrishRoadShortReturn() {
        double[] coords = {51.442, -0.109};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 26446121L).findFirst().get();

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 2500);
        System.out.println(x.getPredecessor() + " hello");

        String str = "";
        str = returnPath(x, str);
        System.out.println(str);
    }


    @Test(timeout=3000)
    public void testMorrishRoadLongerReturn() {
        double[] coords = {51.445, -0.112};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 4898590L).findFirst().get();

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 5000);
        System.out.println(x.getPredecessor() + " hello");

        String str = "";
        str = returnPath(x, str);
        System.out.println(str);
    }

    @Test(timeout=3000)
    public void testCraignairRoadShortReturn() {
        double[] coords = {51.448321, -0.114648};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 12694843L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);


        PathTuple x = returnPath.searchGraph(w, coords, 2500);
        System.out.println(x.getPredecessor() + " hello");

        String str = "";
        str = returnPath(x, str);
        System.out.println(str);


    }

    @Test(timeout=3000)
    public void testCraignairRoadLongerReturn() {
        double[] coords = {51.448321, -0.114648};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 22751151L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 5000);
        System.out.println(x.getPredecessor() + " hello");

        String str = "";
        str = returnPath(x, str);
        System.out.println(str);

    }

    @Test(timeout=3000)
    public void testTulseHillReturn() {
        double[] coords = {51.441109, -0.106974};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 12538762L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 5000);
        System.out.println(x.getPredecessor() + " hello");

        String str = "";
        str = returnPath(x, str);
        System.out.println(str);

    }

}