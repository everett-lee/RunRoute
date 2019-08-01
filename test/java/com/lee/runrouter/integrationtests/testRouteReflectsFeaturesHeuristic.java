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
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGenerator;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGeneratorMain;
import com.lee.runrouter.routegenerator.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class testRouteReflectsFeaturesHeuristic {
    RouteGenerator routeGenerator;
    CycleGenerator cycleGenerator;
    ElementRepo repo;
    Heuristic distanceHeuristic;
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
        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);
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
    public void testHeuristicReflectedOne() throws PathNotGeneratedException {
        double[] coords = {51.446537, -0.124989};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int matchedCountWithoutPref = countMatchedHighways(route);

        FeaturesHeuristic fh = (FeaturesHeuristic) featuresHeuristic;
        fh.setPreferredHighways(preferredHighways);
        route = routeGenerator.generateRoute(coords, 5000);

        int matchedCountWithPref = countMatchedHighways(route);
        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);

        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedTwo() throws PathNotGeneratedException {
        double[] coords = {51.440830, -0.106387};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int matchedCountWithoutPref = countMatchedHighways(route);

        FeaturesHeuristic fh = (FeaturesHeuristic) featuresHeuristic;
        fh.setPreferredHighways(preferredHighways);
        route = routeGenerator.generateRoute(coords, 5000);

        int matchedCountWithPref = countMatchedHighways(route);

        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedThree() throws PathNotGeneratedException {
        double[] coords = {51.461868, -0.115622};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int matchedCountWithoutPref = countMatchedHighways(route);

        FeaturesHeuristic fh = (FeaturesHeuristic) featuresHeuristic;
        fh.setPreferredHighways(preferredHighways);
        route = routeGenerator.generateRoute(coords, 5000);

        int matchedCountWithPref = countMatchedHighways(route);

        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testLitHeuristicReflectedOne() throws PathNotGeneratedException {
        double[] coords = {51.446537, -0.124989};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAllowed = countUnlit(route);

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) inward;
        sa2.setAvoidUnlit(true);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setAvoidUnlit(true);

        route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAvoided = countUnlit(route);

        System.out.println(numberofUnlitWhenUnlitAllowed);
        System.out.println(numberofUnlitWhenUnlitAvoided);
        assertTrue(numberofUnlitWhenUnlitAvoided < numberofUnlitWhenUnlitAllowed);
    }

    @Test
    public void testLitHeuristicReflectedTwo() throws PathNotGeneratedException {
        double[] coords = {51.440830, -0.106387};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAllowed = countUnlit(route);

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) inward;
        sa2.setAvoidUnlit(true);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setAvoidUnlit(true);

        route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAvoided = countUnlit(route);

        System.out.println(numberofUnlitWhenUnlitAllowed);
        System.out.println(numberofUnlitWhenUnlitAvoided);
        assertTrue(numberofUnlitWhenUnlitAvoided < numberofUnlitWhenUnlitAllowed);
    }

    @Test
    public void testLitHeuristicReflectedThree() throws PathNotGeneratedException {
        double[] coords = {51.461868, -0.115622};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAllowed = countUnlit(route);

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) inward;
        sa2.setAvoidUnlit(true);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setAvoidUnlit(true);

        route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAvoided = countUnlit(route);

        System.out.println(numberofUnlitWhenUnlitAllowed);
        System.out.println(numberofUnlitWhenUnlitAvoided);
        assertTrue(numberofUnlitWhenUnlitAvoided < numberofUnlitWhenUnlitAllowed);
    }

    public int countMatchedHighways(PathTuple head) {
        int matchedCount = 0;
        while (head != null) {
            if (preferredHighways.contains(head.getCurrentWay().getHighway())) {
                matchedCount++;
            }
            head = head.getPredecessor();
        }
        return  matchedCount;
    }

    public int countUnlit(PathTuple head) {
        int matchedCount = 0;
        while (head != null) {
            if (!head.getCurrentWay().isLit()) {
                matchedCount++;
            }
            head = head.getPredecessor();
        }
        return  matchedCount;
    }

}