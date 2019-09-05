package com.lee.runrouter.benchmarks;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkRunner {
    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BFS.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .warmupIterations(1)
                .timeUnit(TimeUnit.MILLISECONDS)
                .measurementIterations(50)
                .build();

        new Runner(opt).run();

    }
}