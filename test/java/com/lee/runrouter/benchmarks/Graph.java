package com.lee.runrouter.benchmarks;

import com.lee.runrouter.bbcalculator.BBCalculator;
import com.lee.runrouter.bbcalculator.ScaledBBCalculator;
import com.lee.runrouter.dbconnection.queries.*;
import com.lee.runrouter.graph.elementbuilder.WayBuilder;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.graph.graphbuilder.OriginParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class Graph {
    public void init() {
        Blackhole blackhole = new Blackhole("Blackhole");
        buildGraph5k(blackhole);
        buildGraph14k(blackhole);
        buildGraph21k(blackhole);

    }


    @Benchmark
    public void buildGraph5k(Blackhole blackhole) {
        QueryBuilder queryBuilder = new WayQueryBuilderEnvelope();
        BBCalculator bbCalculator = new ScaledBBCalculator();
        QueryDirector queryDirector = new WayQueryDirectorEnvelope(queryBuilder, bbCalculator);
        QueryBuilder originQueryBuilder = new OriginQueryBuilder();
        QueryDirector originQueryDirector = new OriginQueryDirector(originQueryBuilder, bbCalculator);
        OriginParser originParser = new OriginParser(originQueryDirector);
        WayBuilder wayBuilder = new WayBuilder();
        ElementRepo elementRepo = new ElementRepo();

        GraphBuilder gb = new GraphBuilder(queryDirector, originParser, wayBuilder, elementRepo);

        double distance = 5000;
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446529, -0.125446};
        gb.buildGraph(coords, distance, opts);
        blackhole.consume(elementRepo);
    }

    @Benchmark
    public void buildGraph14k(Blackhole blackhole) {
        QueryBuilder queryBuilder = new WayQueryBuilderEnvelope();
        BBCalculator bbCalculator = new ScaledBBCalculator();
        QueryDirector queryDirector = new WayQueryDirectorEnvelope(queryBuilder, bbCalculator);
        QueryBuilder originQueryBuilder = new OriginQueryBuilder();
        QueryDirector originQueryDirector = new OriginQueryDirector(originQueryBuilder, bbCalculator);
        OriginParser originParser = new OriginParser(originQueryDirector);
        WayBuilder wayBuilder = new WayBuilder();
        ElementRepo elementRepo = new ElementRepo();

        GraphBuilder gb = new GraphBuilder(queryDirector, originParser, wayBuilder, elementRepo);

        double distance = 14000;
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446529, -0.125446};
        gb.buildGraph(coords, distance, opts);
        blackhole.consume(elementRepo);
    }
    @Benchmark
    public void buildGraph21k(Blackhole blackhole) {
        QueryBuilder queryBuilder = new WayQueryBuilderEnvelope();
        BBCalculator bbCalculator = new ScaledBBCalculator();
        QueryDirector queryDirector = new WayQueryDirectorEnvelope(queryBuilder, bbCalculator);
        QueryBuilder originQueryBuilder = new OriginQueryBuilder();
        QueryDirector originQueryDirector = new OriginQueryDirector(originQueryBuilder, bbCalculator);
        OriginParser originParser = new OriginParser(originQueryDirector);
        WayBuilder wayBuilder = new WayBuilder();
        ElementRepo elementRepo = new ElementRepo();

        GraphBuilder gb = new GraphBuilder(queryDirector, originParser, wayBuilder, elementRepo);

        double distance = 21000;
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446529, -0.125446};
        gb.buildGraph(coords, distance, opts);
        blackhole.consume(elementRepo);
    }
}
