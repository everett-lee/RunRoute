package com.lee.runrouter.integrationtests;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.EuclideanCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFS;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFSConnectionPath;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
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
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class testRouteReflectsPreferUnpavedHeuristic {
    RouteGenerator routeGenerator;
    ElementRepo repoSW;
    ElementRepo repoLAW;
    DistanceFromOriginNodeHeursitic distanceHeuristicReturn;
    DistanceFromOriginNodeHeursitic distanceHeuristicDirect;
    DistanceCalculator distanceCalculator;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    GraphSearch outward;
    IteratedLocalSearch iteratedLocalSearch;
    ILSGraphSearch ilsGraphSearch;
    List<String> avoidedPathsInclusive;
    List<String> avoidedPathsExclusive;

    @Before
    public void setUp() {
        repoSW = TestHelpers.getRepoSW();
        repoLAW = TestHelpers.getRepoLAW();
        distanceCalculator = new EuclideanCalculator();

        // heuristics
        distanceHeuristicReturn = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        distanceHeuristicDirect = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();

        outward = new BFS(repoSW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoSW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);

        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);

        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoSW);

        // avoided paths options
        avoidedPathsExclusive = new ArrayList<>(Arrays.asList());
        // avoided paths options
        avoidedPathsInclusive = new ArrayList<>(Arrays.asList("DIRT", "GRASS", "GROUND",
                                                                "EARTH", "SAND", "UNPAVED"));
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
            // set the avoided paths to empty
            fh.setPreferredSurfaces(avoidedPathsExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedSurfaces(route);
            distanceWithoutPref += getDistance(route);

            // set the avoided paths to include concrete
            fh.setPreferredSurfaces(avoidedPathsInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedSurfaces(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref >= matchedCountWithoutPref);
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
            // set the avoided paths to empty
            fh.setPreferredSurfaces(avoidedPathsExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedSurfaces(route);
            distanceWithoutPref += getDistance(route);

            // set the avoided paths to include concrete
            fh.setPreferredSurfaces(avoidedPathsInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedSurfaces(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref >= matchedCountWithoutPref);
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
            // set the avoided paths to empty
            fh.setPreferredSurfaces(avoidedPathsExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedSurfaces(route);
            distanceWithoutPref += getDistance(route);

            // set the avoided paths to include concrete
            fh.setPreferredSurfaces(avoidedPathsInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedSurfaces(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref >= matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedLaw() throws PathNotGeneratedException {
        double[] coords = { 51.9375650, 1.0507934};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(55178471L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the avoided paths to empty
            fh.setPreferredSurfaces(avoidedPathsExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedSurfaces(route);
            distanceWithoutPref += getDistance(route);

            // set the avoided paths to include concrete
            fh.setPreferredSurfaces(avoidedPathsInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedSurfaces(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref >= matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedMan() throws PathNotGeneratedException {
        double[] coords = {51.946379, 1.059363};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734311L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the avoided paths to empty
            fh.setPreferredSurfaces(avoidedPathsExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedSurfaces(route);
            distanceWithoutPref += getDistance(route);

            // set the avoided paths to include concrete
            fh.setPreferredSurfaces(avoidedPathsInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedSurfaces(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref >= matchedCountWithoutPref);
    }

    @Test
    public void testHeuristicReflectedLbo() throws PathNotGeneratedException {
        double[] coords = {51.9199469, 1.0437911};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(58755144L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double matchedCountWithoutPref = 0;
        double matchedCountWithPref = 0;
        double distanceWithoutPref = 0;
        double distanceWithPref = 0;
        FeaturesHeuristic fh = featuresHeuristic;

        for (int i = 0; i < 100; i++) {
            // set the avoided paths to empty
            fh.setPreferredSurfaces(avoidedPathsExclusive);

            PathTuple route = routeGenerator.generateRoute(coords, 10000);

            System.out.println(route.getTotalLength());
            matchedCountWithoutPref += countMatchedSurfaces(route);
            distanceWithoutPref += getDistance(route);

            // set the avoided paths to include concrete
            fh.setPreferredSurfaces(avoidedPathsInclusive);

            route = routeGenerator.generateRoute(coords, 10000);
            matchedCountWithPref += countMatchedSurfaces(route);
            distanceWithPref += getDistance(route);
        }

        System.out.println(matchedCountWithoutPref);
        System.out.println(matchedCountWithPref);
        System.out.println(distanceWithoutPref);
        System.out.println(distanceWithPref);
        assertTrue(matchedCountWithPref >= matchedCountWithoutPref);
    }


    public double getDistance(PathTuple head) {
        while (head.getPredecessor() != null) {
            head = head.getPredecessor();
        }
        return head.getTotalLength();
    }

    public double countMatchedSurfaces(PathTuple head) {
        double matchedDistance = 0;
        while (head != null) {
            if (avoidedPathsInclusive.contains(head.getCurrentWay().getSurface())) {
                matchedDistance += head.getSegmentLength();
            }
            head = head.getPredecessor();
        }
        return  matchedDistance;
    }


}