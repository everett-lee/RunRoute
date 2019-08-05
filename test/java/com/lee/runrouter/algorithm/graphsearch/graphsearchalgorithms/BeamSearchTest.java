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
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.lee.runrouter.testhelpers.TestHelpers.*;


public class BeamSearchTest {
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

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET", "PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristicMain();
        ((FeaturesHeuristicMain) featuresHeuristic).setPreferredHighways(preferredHighways);
        ((FeaturesHeuristicMain) featuresHeuristic).setPreferredSurfaces(preferredSurfaces);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);
        gradientCalculator = new SimpleGradientCalculator();


        beamSearch = new BeamSearch(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

    }

    @Test(timeout = 1000)
    public void testMorrishRoadShort() {
        double[] coords = {51.446810, -0.125484};
        PathTuple x = beamSearch.searchGraph(repo.getOriginWay(), coords, 2500);

    }


    @Test(timeout = 1000)
    public void testMorrishRoadLonger() {

        double[] coords = {51.446810, -0.125484};
        PathTuple x = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);

        System.out.println(calculateScore(x));
    }

    @Test(timeout = 1000)
    public void testCraignairRoadShort() {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = beamSearch.searchGraph(repo.getOriginWay(), coords, 2500);


    }


    @Test(timeout = 1000)
    public void testCraignairRoadLonger() {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = beamSearch.searchGraph(repo.getOriginWay(), coords, 5000);

    }


    @Test(timeout = 1000)
    public void TulseHillTest10KM() {
        double[] coords = {51.441109, -0.106974};

        List<String> preferredHighways = new ArrayList<>(Arrays.asList("FOOTWAY"));
        featuresHeuristic = new FeaturesHeuristicMain();
        ((FeaturesHeuristicMain) featuresHeuristic).setPreferredHighways(preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);
        beamSearch = new BeamSearch(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = beamSearch.searchGraph(repo.getOriginWay(), coords, 10000);


        System.out.println(calculateScore(x));
    }
}