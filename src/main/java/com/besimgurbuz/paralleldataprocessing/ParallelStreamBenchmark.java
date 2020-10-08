package com.besimgurbuz.paralleldataprocessing;

import com.besimgurbuz.forkjoin.Main;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */

@BenchmarkMode(Mode.AverageTime) // Measures the average time take to the benchmarked method
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Prints benchmark results using milliseconds as time unit
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"}) // Executes the benchmark 2 times to increase the reliability of results, with 4Gb of heap space
@State(value = Scope.Benchmark)
@Measurement(iterations = 5)
@Warmup(iterations = 5)
public class ParallelStreamBenchmark {
    private static final long N = 10_000_000L;

//    @Benchmark
//    public long sequentialSum() {
//        return Stream.iterate(1L, i -> i + 1).limit(N)
//                .reduce(0L, Long::sum);
//    }
//
    @Benchmark
    public long iterativeSum() {
        long result = 0;
        for (long i = 1L; i <= N; i++) {
            result += i;
        }
        return result;
    }

//    @Benchmark
//    public long parallelSum() {
//        return Stream.iterate(1L, i -> i + 1).limit(N)
//                .parallel()
//                .reduce(0L, Long::sum);
//    }

    @Benchmark
    public long rangedSum() {
        return LongStream.rangeClosed(1, N)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long forkJoinSumCalculator() {
        return Main.forkJoinSum(N);
    }

    @Benchmark
    public long rangedDirectParallelSum() {
        return LongStream.rangeClosed(1, N).parallel().sum();
    }

    @Benchmark
    public long parallelRangedSum() {
        return LongStream.rangeClosed(1, N)
                .parallel()
                .reduce(0L, Long::sum);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        // Tries to run the garbage collector after each iteration of the benchmark
        System.gc();
    }

    /*
    Finally,  we  got  a  parallel  reduction  that’s  faster  than
    its  sequential  counterpart, because  this  time  the
    reduction  operation  can  be  executed  correctly.This also
    demonstrates that using the right data structure and then
    making it work in parallel guarantees the best performance. Note
    that this latest version is also around20%  faster  than  the
    original  iterative  one,  demonstrating  that,  when  used
    correctly,the  functional-programming  style  allows  us  to
    use  the  parallelism  of  modern  multi-core CPUs in a simpler
    and more straightforward way than its imperative counterpart.
    Nevertheless, keep in mind that parallelization does not come
    for free. The parallelization  process  itself  requires  you
    to  recursively  partition  the  stream,  assign  the reduction
    operation  of  each  sub-stream  to  a  different  thread,  and
    then  combine  the results of these operations in a single
    value. But moving data between multiple cores is also more
    expensive than you might expect, so it’s important that work to
    be done in parallel on another core takes longer than the time
    required to transfer the data from one core to another. In
    general, there are many cases where it isn’t possible or
    convenient to use parallelization. But before you use a
    parallel stream to make your code faster, you have to be sure
    that you’re using it correctly; it’s not helpful to produce
    a result in less time if the result will be wrong.
     */
}
