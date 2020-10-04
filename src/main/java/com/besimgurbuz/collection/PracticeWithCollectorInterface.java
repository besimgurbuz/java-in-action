package com.besimgurbuz.collection;

import com.besimgurbuz.models.Dish;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Besim Gurbuz
 */
public class PracticeWithCollectorInterface {
    /*
        The Collector interface consists of a set of methods that provide a blueprint for how to
    implement specific reduction operations (collectors). You've seen may collectors that implement
    the Collector interface, such as toList or groupingBy. This also implies that you're free to
    create customized reduction operations by providing your own implementation of the Collector
    interface to create a collector to partition a stream of numbers into prime and non-prime more
    efficiently than what you've seen so far.

        To get started with Collector interface, we focus on one of the first collectors you
    encountered at the beginning of this chapter: the toList factory method, which gathers all
    the elements of a stream in a List. We said that you'll frequently use this collector in
    your day-to-day job, but it's also one that, at least conceptually, is straight-forward to
    develop. Investigating in more detail how this collector is implemented is a good way to
    understand how the Collector interface is defined and how the functions returned by its
    methods are internally used by the collect method.

        Let's start by taking a look at the definition of the Collector interface in the next
    listing, which shows the interface signature together with the five methods it declares.


        public interface Collector<T, A, R> {
            Supplier<A> supplier();
            BiConsumer<A, T> accumulator();
            Function<A, R> finisher();
            BinaryOperator<A> combiner();
            Set<Characteristics> characteristics();
        }

     In this listing, the following definitions apply:
        - T is the generic type of the items in the stream to be collected.
        - A is the type of the accumulator, the object on which the partial result will be
        accumulated during the collection process.
        - R is the type of the object (typically, but not always, the collection) resulting from
        the collect operation.

        For instance, you could implement a ToListCollector<T> class that gathers all the elements
     of a Stream<T> into a List<T> having the following signature

        public class ToListCollector<T> implements Collector<T, List<T>, List<T>>

     where, as we'll clarify shortly, the object used for he accumulation process will also be the
     final result of the collection process.
     */

    // Making sense of the methods declared by Collector interface
    /*
    We can now analyze the five methods declared by the Collector interface one by one. When we do
    so, you'll notice that each of the first four methods returns a function that will be invoked
    by the collect method, whereas the fifth one, characteristics, provides a set of
    characteristics that's a list of hits used by the collect method itself to know which
    optimizations (for example, parallelization) it's allowed to employ while performing the
    reduction operation.
     */

    // Making a new result container: The Supplier Method
    /*
    The supplier method has to return a Supplier of an empty accumulator-a parameterless function
    that when invoked creates an instance of an empty accumulator used during the collection
    process. Clearly, for a collector returning the accumulator itself as result, like our
    ToListCollector, this empty accumulator will also represent the result of the collection
    process when performed on an empty stream. In our ToListCollector the supplier will then return
    an empty List, as follows:

        public Supplier<List<T>> supplier() {
            return () -> new ArrayList<T>();
        }

    Note that you could also pass a constructor reference:

        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }
     */

    // Adding an element to a result container: The Accumulator Method
    /*
    The accumulator method returns the function that performs the reduction operation. When
    traversing the nth element in the stream, this function is applied with two arguments, the
    accumulator being the result of the reduction (after having collected the first n-1 items of
    the stream) and the nth element itself. The function returns void because the accumulator is
    modified in place, meaning that its internal state is changed by the function application to
    reflect the effect of the traversed element. For ToListCollector, this function merely has to
    add the current item to the list containing the already traversed ones:

        public BiConsumer<List<T>, T> accumulator() {
            return (list, item) -> list.add(item);
        }
    You could instead use a method reference, which is more concise:

        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }
     */

    // Applying the final transformation to the result container: The Finisher Method
    /*
    The finisher method has to return a function that's invoked at the end of the accumulation
    process, after having completely traversed the stream, in order to transform the accumulator
    object  into the final result of the whole collection operation. Often, as in the case of the
    ToListCollector, the accumulator object already coincides with the final expected result. As
    consequence, there's no need to perform a transformation, so the finisher method has to return
    the identity function:

        public Function<List<T>, List<T>> finisher() {
            return Function.identity();
        }

    These first three methods are enough to execute a sequential reduction of the stream that, at
    least from a logical point of view, could proceed as in figure 6.7. The implementation details
    are a bit more difficult in practice due to both the lazy nature of the stream, which could
    require a pipeline of other intermediate operations to execute before the collect operation
    and the possibility, in theory, of performing the reduction in parallel.
     */

    // Merging two result containers: The Combiner Method
    /*
    The 'combiner' method, the last of the four methods that return a function used by the
    reduction operation, defines how the accumulators resulting from the reduction of different
    sub-parts of the stream are combined when the sub-parts are processed in parallel. In the
    toList case, the implementation of this method is simple; add the list containing the items
    gathered from the second sub-part of the stream to the end of the list obtained when traversing
    the first sub-part:

        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list.addAll(list2);
                return list1;
            }
        }


    The addition of this fourth method allows a parallel reduction of the stream. This uses the
    fork/join framework introduced in Java 7 and the Spliterator abstraction that you'll learn
    about in the next chapter.

        - The original `stream` is recursively split in sub-streams until a condition defining
        whether a stream needs to be further divided becomes false (parallel computing often
        slower than sequential computing when the units of work being distributed are too small,
        and it's pointless to generate many more parallel tasks than you have processing cores).

        - At this point, all sub-streams can be processed in parallel, each of them using the
        sequential reduction algorithm shown in figure 6.7.

        - Finally, all the partial results are combined pairwise using the function returned by the
        combiner method of the collector. This is done by combining results corresponding to
        sub-streams associated with each split of the original stream.
     */

    // The Characteristics Method
    /*
    The last method, characteristics, returns an immutable set of Characteristics, defining the
    behavior of the collector-in particular providing hints about whether the stream can be reduced
    in parallel and which optimizations are valid when doing so. Characteristics is an enumeration
    containing three items:

    - UNORDERED : The result of the reduction isn't affected by the order in which the items in the
    stream are traversed an accumulated.
    - CONCURRENT : The accumulator function can be called concurrently from multiple threads, and
    then this collector can perform a parallel reduction of the stream. If the collector isn't
    also flagged as UNORDERED, it can perform a parallel reduction only when it's applied to an
    unordered data source.
    - IDENTITY_FINISH : This indicates the function returned by the finisher method is the identity
    one, and its application can be omitted. In this case, the accumulator object is directly used
    as the final result of the reduction process. This also implies that it's safe to do an
    unchecked cast from accumulator A to the result R.

    The ToListCollector developed so far is IDENTITY_FINISH, because the List used to accumulate
    the elements in the stream is already the expected final result and doesn't need any further
    transformation, but it isn't UNORDERED because if you apply it to an ordered stream you want
    this ordering to be preserved in the resulting List. Finally, it's CONCURRENT, but following
    what we just said, the stream will be processed in parallel only if tis underlying data source
    is unordered.
     */

    /*
    Putting them all together

    The five methods analyzed in the preceding subsection are everything you need to develop your
    own ToListCollector so you can implement it by putting all of them together, as the next
    listing shows.
     */

    public static class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Function<List<T>, List<T>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.IDENTITY_FINISH, Characteristics.CONCURRENT
            ));
        }
    }
    /*
    Note that this implementation isn't identical to the one returned by the Collectors.toList
    method, but it differs only in some minor optimizations. These optimizations are mostly
    related to the fact that the collector provided by the Java API uses the
    Collections.emptyList() singleton when it has to return an empty list. This means that it
    could be safely used in place of the original Java as an example to gather a list fo all the
    Dishes of a menu stream:
     */

    public static void main(String[] args) {
        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));
        List<Dish> dishes = menu.stream().collect(new ToListCollector<Dish>());

        /*
        The remaining difference from this and the standard formulation

        List<Dish> dishes =  menuStream.collect(toList());

        is  that  toList  is  a  factory,  whereas  you  have  to  use  new  to  instantiate  your
        ToList-Collector.
         */

        // Performing A Custom Collect Without Creating A Collector Implementation
        /*
        In the case of an IDENTITY_FINISH collection operation, there's a further possibility of
        obtaining the same result without developing a completely new implementation of the
        Collector interface. Streams has an overloaded collect method accepting the three other
        functions-supplier, accumulator, and combiner - having exactly the same semantics as the
        ones returned by the corresponding methods of the Collector interface. For instance it's
        possible to collect in a List all the items in a stream of dishes, as follows:
         */
        List<Dish> dishes1 = menu.stream().collect(
                ArrayList::new, // Supplier
                List::add, // Accumulator
                List::addAll // Combiner
        );
        /*
        We believe that this second form, even if more compact and concise than the former one, is
        rather less readable. Also, developing an implementation of your custom collector in a
        proper class promotes its reuse and helps avoid code duplication. It’s also worth  noting
        that  you’re  not  allowed  to  pass  any  Characteristics  to  this  second collect
        method, so it always behaves as an IDENTITY_FINISH and CONCURRENT but not UNORDERED
        collector. In the next section, you’ll take your new knowledge of implementing collectors
        to the  next  level.  You’ll  develop  your  own  custom  collector  for  a  more  complex
        but hopefully more specific and compelling use case.
         */

        // Developing your own collector for better performance
        /*
        In section 6.4, where we discussed partitioning, you created a collector using one of the
        may convenient factory methods provided by the Collectors class, which divides the first n
        natural into primes an non-primes, as shown in the following listing.
         */

        Map<Boolean, List<Integer>> primeOrNot = partitionPrimesWithCustomCollector(100);

        System.out.println("PRIMES:");
        System.out.println(primeOrNot.get(true));
        System.out.println("NOT PRIMES");
        System.out.println(primeOrNot.get(false));
    }

    public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(Collectors.partitioningBy(candidate -> isPrime1(candidate)));
    }
    /*
    There you achieved an improvement over the original isPrime method by limiting the number of
    divisors to be tested against the candidate prime to those not bigger than the candidate's
    square root:
     */
    public static boolean isPrime1(int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }

    /*
    Is there a way to obtain even better performances? The answer is yes, but for this you'll have
    to develop a custom collector.

    where T, A, and R are respectively the type of the elements in the stream, the type of the
    object used to accumulate partial results, and the type of the final result of the collect
    operation. In this case, you want to collect streams of Integers while both the accumulator
    and the result types are Map<Boolean,List<Integer>> (the sameMap you obtained as a result of
    the former partitioning operation in listing 6.6), having as keys true and false and as
    values respectively the Lists of prime and non-prime numbers:
     */

    // ne possible optimization is to test only if the candidate number is divisible by prime
    // numbers. It’s pointless to test it against a divisor that’s not itself prime!
    // so new isPrime function:

    public static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return primes.stream()
                .takeWhile(i -> i <= candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }

    public static class PrimeNumbersCollector implements Collector<Integer,
                                                                Map<Boolean, List<Integer>>,
                                                                Map<Boolean, List<Integer>>> {

        @Override
        public Supplier<Map<Boolean, List<Integer>>> supplier() {
            return () -> new HashMap<Boolean, List<Integer>>() {{
                put(true, new ArrayList<Integer>());
                put(false, new ArrayList<Integer>());
            }};
        }

        @Override
        public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
            return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
                acc.get(isPrime(acc.get(true), candidate)).add(candidate);
            };
        }

        @Override
        public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
            /*
            Note  that  in  reality  this  collector  can’t  be  used  in  parallel,  because  the
            algorithm  is inherently sequential. This means the combiner method won’t ever be
            invoked, and you could leave its implementation empty (or better, throw an
            UnsupportedOperation-Exception). We decided to implement it anyway only for
            completeness.
             */
            return (Map<Boolean, List<Integer>> map1,
                    Map<Boolean, List<Integer>> map2) -> {
               map1.get(true).addAll(map2.get(true));
               map1.get(false).addAll(map2.get(false));
               return map1;
            };
        }

        @Override
        public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
        }
    }

    public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(new PrimeNumbersCollector());
    }
    /*
    Comparing collectors performances

    The collector created with the partitioningBy factory method and the custom one you just
    developed are functionally identical, but you achieve your goal of improving the performance
    of the partitioningBy collector with your custom one? Let's write a quick harness to check
    this:
     */

    public static class CollectorHarness {
        public static void main(String[] args) {
            long fastest = Long.MAX_VALUE;

            for (int i = 0; i < 10; i++) {
                long start = System.nanoTime();
                partitionPrimesWithCustomCollector(1_000_000); // Partitions the first million natural numbers into primes and non-primes
                long duration = (System.nanoTime() - start) / 1_000_000;
                if (duration < fastest) fastest = duration;
            }
            System.out.println(
                    "Fastest execution done in " + fastest + " msecs");
        }
    }

    /*
   Finally, it’s important to note that, as you did for the ToListCollector in listing 6.5, it’s
   possible to obtain the same result by passing the three functions implementing the core logic
   of  PrimeNumbersCollector  to  the  overloaded  version  of  the  collect method, taking them
   as arguments:
     */

    Map<Boolean, List<Integer>> primeOrNotWithCustomCollector =
            IntStream.rangeClosed(2, 1000).boxed()
                .collect(
                        // Supplier
                        () -> new HashMap<Boolean, List<Integer>>() {{
                            put(true, new ArrayList<Integer>());
                            put(false, new ArrayList<Integer>());
                        }},
                        // Accumulator
                        (acc, candidate) -> {
                            acc.get(isPrime(acc.get(true), candidate))
                                    .add(candidate);
                        },
                        // Combiner
                        (map1, map2) -> {
                           map1.get(true).addAll(map2.get(true));
                           map1.get(false).addAll(map2.get(false));
                        }
                );
}
