package com.besimgurbuz.paralleldataprocessing;

import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class Main {
    /*
    * Processing data in parallel with parallel streams
    * Performance analysis of parallel streams
    * The fork/join framework
    * Splitting a stream of data using a Spliterator
     */

    // Sequential Stream
    public static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .reduce(0L, Long::sum);
    }
    /*
    In the previous code, the reduction process used to sum all the numbers sequentially. The
    difference is that the stream is now internally divided in to multiple chunks. As a result, the
    reduction operation can work on the various chunks independently and in parallel. Finally, the same
    reduction operation combines the values resulting from the partial reduction of each sub-stream,
    producing the result of the reduction process on the whole initial stream.
     */
    // Parallel version of above
    public static long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .parallel()
                .reduce(0L, Long::sum);
    }

    /*
    In reality, calling the method parallel on a sequential stream doesn't imply any concrete
    transformation on the stream itself, Internally, a boolean flag is set to signal that you want to
    run in parallel all the operations that follow the invocation to parallel. Similarly, you can turn
    a parallel stream into a sequential one by invoking the method sequential on it. Note that you
    might think that by by combining these two methods you could achieve finer-grained control over
    which operations you want to perform in parallel and which ones sequentially while traversing the
    stream. For example, you could do something like the following:

        stream.parallel()
            .filter(...)
            .sequential()
            .map(...)
            .parallel()
            .reduce();

    But the last call to parallel or sequential wins and effects the pipeline globally. In this
    example, the pipeline will be executed in parallel because that's the last call in the pipeline.
     */
}
