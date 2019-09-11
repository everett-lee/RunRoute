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
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class testRouteReflectsElevationHeuristic {
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
    List<String> preferredHighways;

    @Before
    public void setUp() {
        repoSW = TestHelpers.getRepoSW();
        repoLAW = TestHelpers.getRepoLAW();
        distanceCalculator = new HaversineCalculator();

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

        // preferred Highways options
        preferredHighways = new ArrayList<>(Arrays.asList("CYCLEWAY", "BRIDLEWAY",
                                         "FOOTWAY", "PATH", "TRACK"));
    }

    @Test
    public void testAverageElevationMorr() throws PathNotGeneratedException {
        double[] coords = {51.446537, -0.124989};

        double avgGradientFlat = 0;
        double avgGradientSteep = 0;

        for (int i = 0; i < 100; i++) {
            elevationHeuristic.setOptions(false);
            PathTuple route = routeGenerator.generateRoute(coords, 10000);
            avgGradientFlat += getAverageGradient(route);

            elevationHeuristic.setOptions(true);
            route = routeGenerator.generateRoute(coords, 10000);
            avgGradientSteep += getAverageGradient(route);
        }

        System.out.println(avgGradientFlat);
        System.out.println(avgGradientSteep);
        assertTrue(avgGradientFlat <= avgGradientSteep);

    }

    @Test
    public void testAverageElevationTulse() throws PathNotGeneratedException {
        double[] coords = {51.440830, -0.106387};

        double avgGradientFlat = 0;
        double avgGradientSteep = 0;

        for (int i = 0; i < 100; i++) {
            elevationHeuristic.setOptions(false);
            PathTuple route = routeGenerator.generateRoute(coords, 10000);
            avgGradientFlat += getAverageGradient(route);

            elevationHeuristic.setOptions(true);
            route = routeGenerator.generateRoute(coords, 10000);
            avgGradientSteep += getAverageGradient(route);
        }

        System.out.println(avgGradientFlat);
        System.out.println(avgGradientSteep);
        assertTrue(avgGradientFlat <= avgGradientSteep);
    }


    @Test
    public void testAverageElevationBrix() throws PathNotGeneratedException {
        double[] coords = {51.461868, -0.115622};

        double avgGradientFlat = 0;
        double avgGradientSteep = 0;

        for (int i = 0; i < 100; i++) {
            elevationHeuristic.setOptions(false);
            PathTuple route = routeGenerator.generateRoute(coords, 10000);
            avgGradientFlat += getAverageGradient(route);

            elevationHeuristic.setOptions(true);
            route = routeGenerator.generateRoute(coords, 10000);
            avgGradientSteep += getAverageGradient(route);
        }

        System.out.println(avgGradientFlat);
        System.out.println(avgGradientSteep);

        assertTrue(avgGradientFlat <= avgGradientSteep);
    }

    @Test
    public void testAverageElevationLaw() throws PathNotGeneratedException {
        double[] coords = {51.9375650, 1.0507934};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(55178471L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double avgGradientFlat = 0;
        double avgGradientSteep = 0;

        for (int i = 0; i < 100; i++) {
            elevationHeuristic.setOptions(false);
            PathTuple route = routeGenerator.generateRoute(coords, 10000);
            avgGradientFlat += getAverageGradient(route);

            elevationHeuristic.setOptions(true);
            route = routeGenerator.generateRoute(coords, 10000);
            avgGradientSteep += getAverageGradient(route);
        }

        System.out.println(avgGradientFlat);
        System.out.println(avgGradientSteep);

        assertTrue(avgGradientFlat <= avgGradientSteep);
    }


    @Test
    public void testAverageElevationMan() throws PathNotGeneratedException {
        double[] coords = {51.946379, 1.059363};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734311L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double avgGradientFlat = 0;
        double avgGradientSteep = 0;

        for (int i = 0; i < 100; i++) {
            elevationHeuristic.setOptions(false);
            PathTuple route = routeGenerator.generateRoute(coords, 10000);
            avgGradientFlat += getAverageGradient(route);

            elevationHeuristic.setOptions(true);
            route = routeGenerator.generateRoute(coords, 10000);
            avgGradientSteep += getAverageGradient(route);
        }

        System.out.println(avgGradientFlat);
        System.out.println(avgGradientSteep);

        assertTrue(avgGradientFlat <= avgGradientSteep);
    }

    @Test
    public void testAverageElevationLbo() throws PathNotGeneratedException {
        double[] coords = {51.9199469, 1.0437911};
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(58755144L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double avgGradientFlat = 0;
        double avgGradientSteep = 0;

        for (int i = 0; i < 100; i++) {
            elevationHeuristic.setOptions(false);
            PathTuple route = routeGenerator.generateRoute(coords, 10000);
            avgGradientFlat += getAverageGradient(route);

            elevationHeuristic.setOptions(true);
            route = routeGenerator.generateRoute(coords, 10000);
            avgGradientSteep += getAverageGradient(route);
        }

        System.out.println(avgGradientFlat);
        System.out.println(avgGradientSteep);

        assertTrue(avgGradientFlat <= avgGradientSteep);
    }

    @Test
    public void testmaxElevationReflectedMor()
            throws PathNotGeneratedException {

        double[] coords = {51.446537, -0.124989};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
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
    public void testmaxElevationReflectedTul()
            throws PathNotGeneratedException {

        double[] coords = {51.440830, -0.106387};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);

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
    public void testMaxElevationReflectedBrix()
            throws PathNotGeneratedException {

        double[] coords = {51.461868, -0.115622};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);

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
    public void testMaxElevationReflectedLaw()
            throws PathNotGeneratedException {
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(55178471L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double[] coords = {51.9375650, 1.0507934};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa2 = (SearchAlgorithm) ilsGraphSearch;
        sa2.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);

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
    public void testMaxElevationReflectedMan()
            throws PathNotGeneratedException {
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(239734311L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double[] coords = {51.946379, 1.059363};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa3 = (SearchAlgorithm) ilsGraphSearch;
        sa3.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);

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
    public void testMaxElevationReflectedLbo()
            throws PathNotGeneratedException {
        repoLAW.setOriginWay(repoLAW.getWayRepo().get(58755144L));

        outward = new BFS(repoLAW, distanceHeuristicReturn, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoLAW, distanceHeuristicDirect, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorMain(outward, iteratedLocalSearch, ilsGraphSearch, repoLAW);

        double[] coords = {51.9199469, 1.0437911};
        double maxGradient = 0.05;

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setMaxGradient(maxGradient);
        SearchAlgorithm sa2 = (SearchAlgorithm) ilsGraphSearch;
        sa2.setMaxGradient(maxGradient);

        elevationHeuristic.setOptions(true);
        PathTuple route = routeGenerator.generateRoute(coords, 21000);

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
