package com.besimgurbuz.collection;

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

     */
}
