package com.lee.runrouter.benchmarks;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkRunner {
    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(Graph.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .warmupIterations(1)
                .timeUnit(TimeUnit.MILLISECONDS)
                .measurementTime(TimeValue.milliseconds(1))
                .measurementIterations(1)
                .build();

        new Runner(opt).run();

    }
}