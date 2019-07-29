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
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGenerator;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGeneratorMain;
import com.lee.runrouter.routegenerator.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.*;

public class testRouteReflectsElevationHeursitic {
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
    }

    @Test
    public void testAverageElevationWhenFlatPreferred() throws PathNotGeneratedException {
        double[] coords = {51.446537, -0.124989};
        PathTuple route = routeGenerator.generateRoute(coords, 5000);



    }

    public static double getAverageGradient(PathTuple route) {
        double averageGradient = 0;
        int count = 0;
        return 1;
    }
}
