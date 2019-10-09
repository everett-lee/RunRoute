//package com.lee.runrouter.benchmarks;
//
//
//import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
//import com.lee.runrouter.algorithm.distanceCalculator.EuclideanCalculator;
//import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
//import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
//import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
//import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
//import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
//import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
//import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
//import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
//import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
//import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
//import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
//import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
//import com.lee.runrouter.algorithm.pathnode.PathTuple;
//import com.lee.runrouter.graph.elementrepo.ElementRepo;
//import com.lee.runrouter.testhelpers.TestHelpers;
//import org.openjdk.jmh.annotations.Benchmark;
//import org.openjdk.jmh.annotations.Scope;
//import org.openjdk.jmh.annotations.Setup;
//import org.openjdk.jmh.annotations.State;
//import org.openjdk.jmh.infra.Blackhole;
//
//@State( Scope.Benchmark )
//public class BFS {
//    ElementRepo repo;
//    GraphSearch bfsCycleWithHaverSine;
//    GraphSearch bfsCycleWithEuclidean;
//    DistanceFromOriginNodeHeursitic distanceFromOriginNodeHeursiticWithHaversine;
//    DistanceFromOriginNodeHeursitic distanceFromOriginNodeHeursiticWithEuclidean;
//    DistanceCalculator distanceCalculatorHaversine;
//    DistanceCalculator distanceCalculatorEuclidean;
//    FeaturesHeuristic featuresHeuristic;
//    ElevationHeuristic elevationHeuristic;
//    GradientCalculator gradientCalculator;
//    EdgeDistanceCalculator edgeDistanceCalculatorWithHaversine;
//    EdgeDistanceCalculator edgeDistanceCalculatorWithEuclidean;
//
//    public void init() {
//        Blackhole blackhole = new Blackhole("Blackhole");
//        searchGraph5kHaversine(blackhole);
//        searchGraph5kEuclidean(blackhole);
//        searchGraph14kHaversine(blackhole);
//        searchGraph14kEuclidean(blackhole);
//        searchGraph21kHaversine(blackhole);
//        searchGraph21kEuclidean(blackhole);
//    }
//
//    @Setup
//    public void startUp() {
//        repo = TestHelpers.getRepoSW();
//        distanceCalculatorHaversine = new HaversineCalculator();
//        distanceCalculatorEuclidean = new EuclideanCalculator();
//        distanceFromOriginNodeHeursiticWithHaversine =
//                new DistanceFromOriginNodeHeuristicMain(distanceCalculatorHaversine);
//        distanceFromOriginNodeHeursiticWithEuclidean =
//                new DistanceFromOriginNodeHeuristicMain(distanceCalculatorEuclidean);
//        featuresHeuristic = new FeaturesHeuristicUsingDistance();
//        elevationHeuristic = new ElevationHeuristicMain();
//        gradientCalculator = new SimpleGradientCalculator();
//        edgeDistanceCalculatorWithHaversine
//                = new EdgeDistanceCalculatorMain(distanceCalculatorHaversine);
//        edgeDistanceCalculatorWithEuclidean
//                = new EdgeDistanceCalculatorMain(distanceCalculatorEuclidean);
//
//        bfsCycleWithHaverSine = new com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFS(repo, distanceFromOriginNodeHeursiticWithHaversine,
//                featuresHeuristic, edgeDistanceCalculatorWithHaversine, gradientCalculator, elevationHeuristic);
//        bfsCycleWithEuclidean = new com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFS(repo, distanceFromOriginNodeHeursiticWithHaversine,
//                featuresHeuristic, edgeDistanceCalculatorWithEuclidean, gradientCalculator, elevationHeuristic);
//    }
//
//    @Benchmark
//    public void searchGraph5kHaversine(Blackhole blackhole) {
//        double[] coords = {51.446810, -0.125484};
//        PathTuple res  = bfsCycleWithHaverSine.searchGraph(repo.getOriginWay(), coords, 5000);
//        blackhole.consume(res);
//    }
//
//    @Benchmark
//    public void searchGraph5kEuclidean(Blackhole blackhole) {
//        double[] coords = {51.446810, -0.125484};
//        PathTuple res  = bfsCycleWithEuclidean.searchGraph(repo.getOriginWay(), coords, 5000);
//        blackhole.consume(res);
//    }
//
//    @Benchmark
//    public void searchGraph14kHaversine(Blackhole blackhole) {
//        double[] coords = {51.446810, -0.125484};
//        PathTuple res  = bfsCycleWithHaverSine.searchGraph(repo.getOriginWay(), coords, 14000);
//        blackhole.consume(res);
//    }
//
//    @Benchmark
//    public void searchGraph14kEuclidean(Blackhole blackhole) {
//        double[] coords = {51.446810, -0.125484};
//        PathTuple res  = bfsCycleWithEuclidean.searchGraph(repo.getOriginWay(), coords, 14000);
//        blackhole.consume(res);
//    }
//
//    @Benchmark
//    public void searchGraph21kHaversine(Blackhole blackhole) {
//        double[] coords = {51.446810, -0.125484};
//        PathTuple res  = bfsCycleWithHaverSine.searchGraph(repo.getOriginWay(), coords, 21000);
//        blackhole.consume(res);
//    }
//
//    @Benchmark
//    public void searchGraph21kEuclidean(Blackhole blackhole) {
//        double[] coords = {51.446810, -0.125484};
//        PathTuple res  = bfsCycleWithEuclidean.searchGraph(repo.getOriginWay(), coords, 21000);
//        blackhole.consume(res);
//    }
//}
