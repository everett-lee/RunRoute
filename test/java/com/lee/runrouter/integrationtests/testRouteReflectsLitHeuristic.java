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

import static org.junit.Assert.*;

public class testRouteReflectsLitHeuristic {
    RouteGenerator routeGenerator;
    ElementRepo repoSW;
    ElementRepo repoLAW;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    DistanceCalculator distanceCalculator;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    GraphSearch outward;
    IteratedLocalSearch iteratedLocalSearch;
    ILSGraphSearch ilsGraphSearch;

    @Before
    public void setUp() {
        repoSW = TestHelpers.getRepoSW();
        repoLAW = TestHelpers.getRepoLAW();
        distanceCalculator = new HaversineCalculator();

        // heuristics
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();

        outward = new BFS(repoSW, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        ilsGraphSearch = new BFSConnectionPath(repoSW, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        iteratedLocalSearch = new IteratedLocalSearchMain(ilsGraphSearch);
        routeGenerator = new RouteGeneratorCycle(outward, iteratedLocalSearch, ilsGraphSearch, repoSW);

    }

    @Test
    public void testLitHeuristicReflectedMorr() throws PathNotGeneratedException {
        double[] coords = {51.446, -0.124};
        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) ilsGraphSearch;
        sa2.setAvoidUnlit(true);
        PathTuple route = routeGenerator.generateRoute(coords, 10000);
        boolean flag = containsUnlit(route);

        assertFalse(flag);
    }

    @Test
    public void testLitHeuristicReflectedTulse() throws PathNotGeneratedException {
        double[] coords = {51.440830, -0.106387};
        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) ilsGraphSearch;
        sa2.setAvoidUnlit(true);
        PathTuple route = routeGenerator.generateRoute(coords, 10000);
        boolean flag = containsUnlit(route);

        assertFalse(flag);
    }


    @Test
    public void testLitHeuristicReflectedBrixton() throws PathNotGeneratedException {
        double[] coords = {51.461868, -0.115622};
        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) ilsGraphSearch;
        sa2.setAvoidUnlit(true);
        PathTuple route = routeGenerator.generateRoute(coords, 10000);
        boolean flag = containsUnlit(route);

        assertFalse(flag);
    }


    public boolean containsUnlit(PathTuple head) {
        while (head != null) {
            if (!head.getCurrentWay().isLit()) {
                System.out.println(head.getCurrentWay().getId());
                return true;
            }
            head = head.getPredecessor();
        }
        return false;
    }

}