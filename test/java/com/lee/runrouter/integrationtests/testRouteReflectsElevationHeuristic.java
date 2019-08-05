package com.lee.runrouter.integrationtests;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.*;
import com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch.IteratedLocalSearch;
import com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch.IteratedLocalSearchMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicMain;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGenerator;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGeneratorMain;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class testRouteReflectsElevationHeuristic {
    RouteGenerator routeGenerator;
    CycleGenerator cycleGenerator;
    ElementRepo repo;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    DistanceCalculator distanceCalculator;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    GraphSearch outward;
    GraphSearch inward;
    IteratedLocalSearch iteratedLocalSearch;
    ILSGraphSearch ilsGraphSearch;
    List<String> preferredHighways;

    @Before
    public void setUp() {
        repo = TestHelpers.getRepo();
        distanceCalculator = new HaversineCalculator();

        // heuristics
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        featuresHeuristic = new FeaturesHeuristicMain();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();

        outward = new BeamSearch(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                                 gradientCalculator, elevationHeuristic);
        inward = new BeamSearchReturnPath(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                                          gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);

        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);

        cycleGenerator = new CycleGeneratorMain(outward, inward, repo);
        routeGenerator = new RouteGeneratorMain(cycleGenerator, iteratedLocalSearch);

        // preferred Highways options
        preferredHighways = new ArrayList<>(Arrays.asList("CYCLEWAY", "BRIDLEWAY",
                                         "FOOTWAY", "PATH", "TRACK"));
    }

    @Test
    public void testAverageElevationWhenLessWhenFlatPreferredOne() throws PathNotGeneratedException {
        double[] coords = {51.446537, -0.124989};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        double avgGradientFlat = getAverageGradient(route);

        elevationHeuristic.setOptions(true);
        route = routeGenerator.generateRoute(coords, 5000);
        double avgGradientSteep = getAverageGradient(route);

        assertTrue(avgGradientFlat < avgGradientSteep);

    }

    @Test
    public void testAverageElevationWhenLessWhenFlatPreferredTwo() throws PathNotGeneratedException {
        double[] coords = {51.440830, -0.106387};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        double avgGradientFlat = getAverageGradient(route);

        elevationHeuristic.setOptions(true);
        route = routeGenerator.generateRoute(coords, 5000);
        double avgGradientSteep = getAverageGradient(route);

        assertTrue(avgGradientFlat < avgGradientSteep);
    }


    @Test
    public void testAverageElevationWhenLessWhenFlatPreferredThree() throws PathNotGeneratedException {
        double[] coords = {51.461868, -0.115622};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        double avgGradientFlat = getAverageGradient(route);

        elevationHeuristic.setOptions(true);
        route = routeGenerator.generateRoute(coords, 5000);
        double avgGradientSteep = getAverageGradient(route);

        assertTrue(avgGradientFlat < avgGradientSteep);

    }

    @Test
    public void testAverageElevationWhenLessWhenFlatPreferredAndFeaturesIncluded()
            throws PathNotGeneratedException {
        FeaturesHeuristic fh = (FeaturesHeuristic) featuresHeuristic;
        fh.setPreferredHighways(preferredHighways);

        double[] coords = {51.446537, -0.124989};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        double avgGradientFlat = getAverageGradient(route);

        elevationHeuristic.setOptions(true);
        route = routeGenerator.generateRoute(coords, 5000);
        double avgGradientSteep = getAverageGradient(route);

        assertTrue(avgGradientFlat < avgGradientSteep);
    }


    @Test
    public void testAverageElevationWhenLessWhenFlatPreferredAndFeaturesIncludedTwo()
            throws PathNotGeneratedException {
        FeaturesHeuristic fh = (FeaturesHeuristic) featuresHeuristic;
        fh.setPreferredHighways(preferredHighways);

        double[] coords = {51.440830, -0.106387};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        double avgGradientFlat = getAverageGradient(route);

        elevationHeuristic.setOptions(true);
        route = routeGenerator.generateRoute(coords, 5000);
        double avgGradientSteep = getAverageGradient(route);


        assertTrue(avgGradientFlat < avgGradientSteep);
    }


    @Test
    public void testAverageElevationWhenLessWhenFlatPreferredAndFeaturesIncludedThree()
            throws PathNotGeneratedException {
        FeaturesHeuristic fh = (FeaturesHeuristic) featuresHeuristic;
        fh.setPreferredHighways(preferredHighways);

        double[] coords = {51.461868, -0.115622};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        double avgGradientFlat = getAverageGradient(route);

        elevationHeuristic.setOptions(true);
        route = routeGenerator.generateRoute(coords, 5000);
        double avgGradientSteep = getAverageGradient(route);

        assertTrue(avgGradientFlat < avgGradientSteep);
    }


    @Test
    public void testmaxElevationReflected()
            throws PathNotGeneratedException {

        double[] coords = {51.446537, -0.124989};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa2 = (SearchAlgorithm) inward;
        sa2.setMaxGradient(maxGradient);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);

        boolean flag = true;

        PathTuple head = route;
        while (head != null) {
            if (head.getSegmentGradient() > maxGradient) {
                System.out.println(head.getSegmentGradient());
                flag = false;
            }
            head = head.getPredecessor();
        }

        // route has max incline <= 0.05
        assertTrue(flag);
    }

    @Test
    public void testmaxElevationReflectedTwo()
            throws PathNotGeneratedException {

        double[] coords = {51.440830, -0.106387};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa2 = (SearchAlgorithm) inward;
        sa2.setMaxGradient(maxGradient);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);
        double avgGradientSteep = getAverageGradient(route);

        boolean flag = true;

        PathTuple head = route;
        while (head != null) {
            if (head.getSegmentGradient() > maxGradient) {
                flag = false;
            }
            head = head.getPredecessor();
        }

        // route has max incline <= 0.05
        assertTrue(flag);
    }

    @Test
    public void testMaxElevationReflectedThree()
            throws PathNotGeneratedException {

        double[] coords = {51.461868, -0.115622};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa2 = (SearchAlgorithm) inward;
        sa2.setMaxGradient(maxGradient);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);
        double avgGradientSteep = getAverageGradient(route);

        boolean flag = true;

        PathTuple head = route;
        while (head != null) {
            if (head.getSegmentGradient() > maxGradient) {
                flag = false;
            }
            head = head.getPredecessor();
        }

        // route has max incline <= 0.05
        assertTrue(flag);
    }


    public static double getAverageGradient(PathTuple route) {
        double summedGradient = 0;
        int count = 0;

        PathTuple head = route;
        while (head != null) {
            count++;
            summedGradient += head.getSegmentGradient();
            head = head.getPredecessor();
        }

        return summedGradient / count;
    }
}
