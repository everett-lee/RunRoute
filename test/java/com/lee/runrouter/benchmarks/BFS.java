package com.lee.runrouter.benchmarks;


import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State( Scope.Benchmark )
public class BFS {
    ElementRepo repo;
    GraphSearch bfsCycle;
    DistanceFromOriginNodeHeursitic distanceFromOriginNodeHeursitic;
    DistanceCalculator distanceCalculator;
    FeaturesHeuristic featuresHeuristic;
    ElevationHeuristic elevationHeuristic;
    GradientCalculator gradientCalculator;
    EdgeDistanceCalculator edgeDistanceCalculator;

    public void init() {
        Blackhole blackhole = new Blackhole("Blackhole");
        buildGraphMorrish5k(blackhole);
    }

    @Setup
    public void startUp() {
        repo = TestHelpers.getRepo();
        distanceCalculator = new HaversineCalculator();
        distanceFromOriginNodeHeursitic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        elevationHeuristic = new ElevationHeuristicMain();
        gradientCalculator = new SimpleGradientCalculator();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);

        bfsCycle = new com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFS(repo, distanceFromOriginNodeHeursitic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
    }

    @Benchmark
    public void buildGraphMorrish5k(Blackhole blackhole) {
        double[] coords = {51.446810, -0.125484};
        PathTuple res  = bfsCycle.searchGraph(repo.getOriginWay(), coords, 5000);
        blackhole.consume(res);
    }

    @Benchmark
    public void buildGraph14k(Blackhole blackhole) {

    }

    @Benchmark
    public void buildGraph21k(Blackhole blackhole) {

    }
}
