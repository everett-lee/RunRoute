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
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorCycle;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class testRouteReflectsBackroadsHeuristic {
    RouteGenerator routeGenerator;
    ElementRepo repo;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    DistanceCalculator distanceCalculator;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    GraphSearch outward;
    IteratedLocalSearch iteratedLocalSearch;
    ILSGraphSearch ilsGraphSearch;
    List<String> preferredHighwaysInclusive;
    List<String> preferredHighwaysExclusive;

    @Before
    public void setUp() {
        repo = TestHelpers.getRepo();
        distanceCalculator = new HaversineCalculator();

        // heuristics
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();

        outward = new BFS(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);

        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);

        routeGenerator = new RouteGeneratorCycle(outward, iteratedLocalSearch, ilsGraphSearch, repo);

        // preferred Highways options
        preferredHighwaysExclusive = new ArrayList<>(Arrays.asList());
        // preferred Highways options
        preferredHighwaysInclusive = new ArrayList<>(Arrays.asList("CYCLEWAY", "BRIDLEWAY",
                "FOOTWAY", "PATH", "TRACK"));


    }

    @Test
    public void testHeuristicReflectedMorrish() throws PathNotGeneratedException {
        double[] coords = {51.446537, -0.124989};

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the preferred Highways to empty
            fh.setPreferredHighways(preferredHighwaysExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedHighways(route);
            distanceWithoutPref += getDistance(route);

            // set the preferred highways to include unpaved
            fh.setPreferredHighways(preferredHighwaysInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedHighways(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedTulse() throws PathNotGeneratedException {
        double[] coords = {51.440830, -0.106387};

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the preferred Highways to empty
            fh.setPreferredHighways(preferredHighwaysExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedHighways(route);
            distanceWithoutPref += getDistance(route);

            // set the preferred highways to include unpaved
            fh.setPreferredHighways(preferredHighwaysInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedHighways(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedBrixton() throws PathNotGeneratedException {
        double[] coords = {51.461868, -0.115622};

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the preferred Highways to empty
            fh.setPreferredHighways(preferredHighwaysExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedHighways(route);
            distanceWithoutPref += getDistance(route);

            // set the preferred highways to include unpaved
            fh.setPreferredHighways(preferredHighwaysInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedHighways(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedLaw() throws PathNotGeneratedException {
        double[] coords = {51.937507, 1.050645};

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the preferred Highways to empty
            fh.setPreferredHighways(preferredHighwaysExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedHighways(route);
            distanceWithoutPref += getDistance(route);

            // set the preferred highways to include unpaved
            fh.setPreferredHighways(preferredHighwaysInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedHighways(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedMan() throws PathNotGeneratedException {
        double[] coords = {51.946379, 1.059363};

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the preferred Highways to empty
            fh.setPreferredHighways(preferredHighwaysExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedHighways(route);
            distanceWithoutPref += getDistance(route);

            // set the preferred highways to include unpaved
            fh.setPreferredHighways(preferredHighwaysInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedHighways(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedLbo() throws PathNotGeneratedException {
        double[] coords = {51.919993, 1.044527};

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the preferred Highways to empty
            fh.setPreferredHighways(preferredHighwaysExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedHighways(route);
            distanceWithoutPref += getDistance(route);

            // set the preferred highways to include unpaved
            fh.setPreferredHighways(preferredHighwaysInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedHighways(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref > matchedCountWithoutPref);
    }


    public double getDistance(PathTuple head) {
        while (head.getPredecessor() != null) {
            head = head.getPredecessor();
        }
        return head.getTotalLength();
    }

    public double countMatchedHighways(PathTuple head) {
        double matchedDistance = 0;
        while (head != null) {
            if (preferredHighwaysInclusive.contains(head.getCurrentWay().getHighway())) {
                matchedDistance += head.getSegmentLength();
            }
            head = head.getPredecessor();
        }
        return  matchedDistance;
    }


}