package com.besimgurbuz.streams;

import com.besimgurbuz.models.Dish;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class StreamsPractice {
    /**
     * What exactly is a stream? A short definition is "a sequence of elements from a source that
     * supports data-processing operations" Let's break down this definition step-by-step:
     *
     *  * Sequence of elements - Like a collection, a stream provides an interface to a sequenced
     *  set of values of a specific element type. Because collections are data structures, they're
     *  mostly about storing and accessing elements with specific time/space complexities (for
     *  example, an ArrayList versus a LikedList). But streams are about expressing computations
     *  such as filter, sorted and map, which you saw earlier. Collections are about data; streams
     *  are about computations.
     *
     *  * Source - Streams consume from a data-providing source such as collections, arrays, or I/O
     *  resources. Note that generating stream from an ordered collection preserves ordering. The
     *  elements of a stream coming from a list will have the same order as the list.
     *
     *  * Data-processing operations - Streams support database-like operations and common
     *  operations from functional programming languages to manipulate data, such as filter, map,
     *  reduce, find, match, sort and so on,. Stream operations can be executed either
     *  sequentially or in parallel.
     *
     * In addition, stream operations have two important characteristics:
     *
     *  * Pipelining - Many stream operations return a stream themselves, allowing operations to
     *  be chained to form a larger pipeline. This enables certain optimizations, such as laziness
     *  and short-circuiting. A pipeline of operations can be viewed as a database-like query on
     *  the data source.
     *
     *  * Internal iteration - In contrast to collections, which iterated explicitly using an
     *  iterator, stream operations do the iteration behind the scenes for you.
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
                new Dish("salmon", false, 450, Dish.Type.FISH) );

        List<String> lowCaloricDishNames =
                menu.stream()
                        .filter(dish -> dish.getCalories() < 400)
                        .sorted(Comparator.comparing(Dish::getCalories))
                        .map(Dish::getName)
                        .collect(Collectors.toList());

        // To exploit a multicore architecture and execute this code in parallel, you need only to
        // change stream() to parallelStream()
        List<String> lowCaloricDishes =
                menu.parallelStream()
                    .filter(dish -> dish.getCalories() < 400)
                    .sorted(Comparator.comparing(Dish::getCalories))
                    .map(Dish::getName)
                    .collect(Collectors.toList());

        lowCaloricDishes.forEach(System.out::println);

        List<String> threeHighCaloricDishNames =
                menu.stream()
                    .filter(dish -> dish.getCalories() > 300)
                    .map(Dish::getName)
                    .limit(3)
                    .collect(Collectors.toList());
        System.out.println(threeHighCaloricDishNames);

        /*
         * In this example, you first get a stream from the list of dishes by calling the stream
         * method on menu. The data source is the list of dishes (the menu) and it provides a
         * sequence of elements to the stream. Next, you apply a series of data-processing
         * operations on the stream: filter, map, limit, and collect. All these operations except
         * collect return another stream so they can be connected to form a pipeline, which can be
         * viewed as a query on the source. Finally, the collect operation starts processing the
         * pipeline to return a result (it's different because it ret;urns something other than a
         * stream-here a List). No result is invoked. You can think of it as if the method
         * invocation in the chain are queued up until collect is called.
         */

        // TRAVERSABLE ONLY ONCE
        /*
        Note that, similarly to iterators, a stream can be traversed only once. After that a stream
        is said to be consumed. You can get a new stream from the initial data source to traverse
        it again as you would for an iterator (assuming it's a repeatable source like a collection;
        if it's an I/O channel, you're out of luck.). For example, the following code would throw
        an exception indicating the stream has been consumed:
         */
        List<String> title = Arrays.asList("Modern", "Java", "In", "Action");
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        // s.forEach(System.out::println); // java.lang.IllegalStateException will be thrown

        // Keep in mind that you can consume a stream only once!


        // Intermediate Operations
        List<String> highCaloricDish = menu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .map(Dish::getName)
                .collect(Collectors.toList());
        /*
        Intermediate operations such as filter sor sorted return another stream as the return
        type. This allows the operations to be connected to form a query. What's important is that
        intermediate operations don't perform any processing until a terminal operation is invoked
        on the stream pipeline-they're lazy. This is because intermediate operations can usually
        be merged and processed into s single pass by the terminal operation.

        To understand what's happening in the stream pipeline, modify the code so each lambda also
        prints the current dish it's processing. (Like many demonstration and debugging
        techniques, this is appalling programming style for production code, but directly explains
        the order of evaluation when you're learning.
         */

        List<String> names =
                menu.stream()
                        .filter(dish -> {
                            System.out.println("filtering: " + dish.getName());
                            return dish.getCalories() > 300;
                        })
                        .map(dish -> {
                            System.out.println("mapping: " + dish.getName());
                            return dish.getName();
                        })
                        .limit(3)
                        .collect(Collectors.toList());
        /*
        By doing this, you can notice that the Streams library performs several optimizations
        exploiting the lazy nature of streams. First despite the fact that many dishes have more
        than 300 calories, only the first three are selected! This is because of the limit
        operation and a technique called short-circuiting, as we'll explain in the next chapter.
        Second, despite the fact that filter and map are two separate operations, they were merged
        into the same pass (compiler experts call this technique loop fusion).
         */

        long count = menu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .distinct() // returns different elements
                .limit(3)
                .count();
        System.out.println(count);

        /*
        WORKING WITH STREAMS
        To summarize, working with stream in general involves three items:
            * A data source (such as collection) to perform query on
            * A chain of intermediate operations that form a stream pipeline
            * A terminal operation that executes the stream pipeline and produces a result
         */

    }
}
