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

public class testRouteReflectsLitHeuristic {
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
    public void testLitHeuristicReflectedOne() throws PathNotGeneratedException {
        double[] coords = {51.441, -0.125   };
        PathTuple route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAllowed = countUnlit(route);

        SearchAlgorithm sa1 = (SearchAlgorithm) outward;
        sa1.setAvoidUnlit(true);
        SearchAlgorithm sa2 = (SearchAlgorithm) ilsGraphSearch;
        sa2.setAvoidUnlit(true);

        route = routeGenerator.generateRoute(coords, 5000);

        int numberofUnlitWhenUnlitAvoided = countUnlit(route);

        System.out.println(numberofUnlitWhenUnlitAllowed);
        System.out.println(numberofUnlitWhenUnlitAvoided);
        assertTrue(numberofUnlitWhenUnlitAvoided < numberofUnlitWhenUnlitAllowed);
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