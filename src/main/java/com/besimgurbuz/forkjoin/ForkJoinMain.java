package com.besimgurbuz.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * @author Besim Gurbuz
 */
public class ForkJoinMain {
    /*
    The fork/join framework was designed to recursively split a parallelizable task into smaller tasks
    and then combine the results of each subtask to produce the overall result. It's an implementation
    of the ExecutorService interface, which distributes those subtasks to worker threads in a thread
    pool, called ForkJoinPool. Let's start by exploring how to define a task and subtasks.

    Working with RecursiveTask

    To submit tasks to this pool, you have to create a subclass of RecursiveTask<R>, where R is the type
    of the result produced by the parallelized task (and each of its subtasks) or of RecursiveAction if
    the task returns no result (it could be updating other non-local structures, though). To define
    RecursiveTasks you need only implement its single abstract method compute.

    This method defines both the logic of splitting the task at hand into subtasks and the algorithm to
    produce the result of a single subtask when it's no longer possible or convenient to further divide
    it. For this reason implementation of this method often resembles the following pseudocode:

    if (task is small enough or no longer divisible) {
        compute task sequentially
    } else {
        split task in two subtasks
        call this method recursively possibly further splitting each subtask
        wait for the completion of all subtasks
        combine the results of each subtask
    }
     */

    public static class ForkJoinSumCalculator extends RecursiveTask<Long> {

        private final long[]  numbers; // the array of numbers to be summed
        private final int start; // the initial and final positions of the sub-array
        private final int end;
        public static final long THRESHOLD = 10_000; // the size threshold for splitting into sub-tasks

        public ForkJoinSumCalculator(long[] numbers) { // public constructor to create the main task
            this(numbers, 0, numbers.length);
        }
        // private constructor to create sub-tasks of the main task
        private ForkJoinSumCalculator(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start; // the size of the sub-array summed by this task
            if (length <= THRESHOLD) {
                return computeSequentially(); // if the size is less than or equal to the threshold, computes the result sequentially
            }
            // Creates a sub-task to sum the first half of the array
            ForkJoinSumCalculator leftTask =
                    new ForkJoinSumCalculator(numbers, start, start + length/2);

            // Asynchronously executes the newly created subtask using another thread of ForkJoinPool
            leftTask.fork();

            // Creates a sub-task to sum the second half of the array
            ForkJoinSumCalculator rightTask =
                    new ForkJoinSumCalculator(numbers, start + length/2, end);

            // Executes this second sub-task synchronously, potentially allowing further recursive splits
            Long rightResult = rightTask.compute();

            // Reads the result of the first sub-task - waiting if it isn't ready
            Long leftResult = leftTask.join();

            // combine the results fo the two sub-tasks
            return leftResult + rightResult;
        }

        private long computeSequentially() {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        }
    }
    /*
    Writing a method performing a parallel sum of the first `n` natural numbers is now straightforward.
    You need to pass the desired array of numbers to the constructor of `ForkJoinSumCalculator`
     */

    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(0, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }
    /*
    Here,  you  generate  an  array  containing  the  first  n  natural  numbers  using  a
    LongStream. Then you create a ForkJoinTask (the superclass of RecursiveTask), passing this array
    to the public constructor of the ForkJoinSumCalculator shown in listing 7.2.Finally, you create a
    new ForkJoinPool and pass that task to its invoke method. The value returned by this last method
    is the result of the task defined by the ForkJoin-SumCalculator class when executed inside the
    ForkJoinPool.

    Note  that  in  a  real-world  application,  it  doesn’t  make  sense  to  use  more  than
    oneForkJoinPool.  For  this  reason,  what  you  typically  should  do  is  instantiate  it  only
    once and keep this instance in a static field, making it a singleton, so it could be conveniently
    reused by any part of your software. Here, to create it you’re using its default no-argument
    constructor, meaning that you want to allow the pool to use all the processors available to the
    JVM. More precisely, this constructor will use the value returned byRuntime. availableProcessors
    to determine the number of threads used by the pool.Note that the availableProcessors  method,
    despite  its  name,  in  reality  returns  the number of available cores, including any virtual
    ones due to hyper-threading.
     */
    public static void main(String[] args) {
        long sum = forkJoinSum(10_000_000);
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println(sum);
    }
}
