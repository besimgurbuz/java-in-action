package com.besimgurbuz.streams;

import com.besimgurbuz.models.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class WorkingWithStreamsNumeric {
    /*
    You saw earlier that you could use the reduce method to calculate the sum of the elements of a
    stream. For example, you can calculate the number of calories in the menu as follows:
        int calories = menu.stream()
                .map(Dish::getCalories)
                .reduce(0, Integer::sum);
    The problem with this code is that there's an insidious boxing cost. Behind the scenes each
    Integer needs to be unboxed to a primitive before performing the summation. In addition,
    wouldn't it be nicer if you could call a sum method directly as follows?
        int calories = menu.stream()
                .map(Dish::getCalories)
                .sum();
    But this isn't possible. The problem is that the method map generates a Stream<T>. Even though
    the elements of the stream are of type Integer, the streams interface doesn't define a sum
    method. Why not? Say you had only a Stream<Dish> like the menu; it wouldn't make any sense to be
    able to sum dishes. But don't worry; the Streams API also supplies primitive stream
    specializations that support specialized methods to work with streams of number.
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
        // Primitive Stream Specializations
        /*
        Java 8 introduces three primitive specialized stream interfaces to tackle this issue,
        IntStream, DoubleStream, and LongStream, which respectively specialize the elements of a
        stream to be int, long and double-and thereby avoid hidden boxing costs. Each of these
        interfaces brings new methods to perform common numeric reductions, such as sum to
        calculate the sum of a numeric stream and max to find the maximum element. In addition,
        they have methods to convert back to a stream of objects when necessary. The thing to
        remember is that the additional complexity of these specializations isn't inherent to
        streams. It reflects the complexity of boxing-the (efficiency-based) difference between
        int and Integer and so on.
         */

        // Mapping to A Numeric Stream
        /*
        The most common methods you'll use to convert a stream to a specialized version are mapToInt
        , mapToDouble, and mapToLong. These methods work exactly like the method map that you saw
        earlier but return a specialized stream instead of a Stream<T>. For example, you can use
        mapToInt as follows to calculate the sum of calories in the menu:
         */
        int calories = menu.stream()
                .mapToInt(Dish::getCalories)
                .sum();
        System.out.println("Total calories of the menu: " + calories);

        // Converting back to Stream of Objects
        /*
        Similarly, once you have a numeric stream, you may be interested in converting it back to
        a nonspecialized stream. For example, the operations of an IntStream are restricted to
        produce primitive integers: the map operation of an IntStream takes a lambda that takes an
        int and produces an int (an IntUnaryOperator). But you may want to produce a different
        value such as a Dish. For this you need to access the operations defined in the Streams
        interface that are more general. To convert from a primitive stream to general stream (each
        int will be boxed to an Integer) you can use the method boxed, as follows:
         */
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        Stream<Integer> stream = intStream.boxed();

        // Boxed is particularly useful when you deal with numeric ranges that need to be boxed into
        // a general stream.

        // Default Values: OptionalInt
        /*
        The sum example was convenient because it has a default value: 0. But if you want to
        calculate the maximum element in an IntStream, you'll need something different because 0 is
        a wrong result. How can you differentiate that the stream has no element and that the real
        maximum is 0? Earlier we introduced the Optional class, which is a container that indicates
        the presence or absence of a value. Optional can be parameterized with reference types such
        as Integer, String and son on. There's a primitive specialized version of Optional as well
        for the three primitive stream specializations: OptionalInt, OptionalDouble and
        OptionalLong

        For example you can find the maximal element of an IntStream by calling the max method,
        which returns an OptionalInt:
        */
        OptionalInt maxCalories = menu.stream()
                .mapToInt(Dish::getCalories)
                .max();

        int max = maxCalories.orElse(1);
        System.out.println("Maximum calorie is: " + max);

        // Numeric Ranges
        /*
        A common use case when dealing with numbers is working with ranges of numeric values. For
        example, suppose you'd like to generate all numbers between 1 and 100. Java 8 introduces
        two static methods available on IntStream and LongStream to help generate such ranges:
        range and rangeClosed. Both methods take the starting value of the range as the first
        parameter and the end value of the range as the second parameter. But range is exclusive,
        whereas rangeClosed is inclusive. Let's look at an example:
         */
        IntStream evenNumbers = IntStream.rangeClosed(1, 100)
                .filter(n -> n % 2 == 0);
        System.out.println(evenNumbers.count());

        IntStream range = IntStream.range(0, 5);
        IntStream rangeClosed = IntStream.rangeClosed(0, 5);

        range.forEach(System.out::print);
        System.out.println("\nRange closed");
        rangeClosed.forEach(System.out::print);

        /*
        Here you use the rangeClosed method to generate a range of all numbers from 1 to 100. It
        produces a stream so you can chain the filter method to select only even numbers. At this
        stage no computation has been done. Finally, you call count on the resulting stream.
        Because count is a terminal operation, it will process the stream and return the result 50,
        which is the number of even numbers from 1 to 100, inclusive. Note that by comparison, if
        you were using IntStream.range(1, 100) instead, the result would be 49 even numbers because
        range is exclusive.
         */
        practice_pythagorean_triples();
    }

    public static void practice_pythagorean_triples() {
        /*
        Now we’ll look at a more difficult example so you can solidify what you’ve learned
        about numeric streams and all the stream operations you’ve learned so far. Your mission,
        if you choose to accept it, is to create a stream of Pythagorean triples.

        PYTHAGOREAN TRIPLE
        What’s a Pythagorean triple? We have to go back a few years in the past. In one of your
        exciting math classes, you learned that the famous Greek mathematician Pythagoras
        discovered that certain triples of numbers (a, b, c) satisfy the formula a * a + b * b =
        c * c where a, b, and c are integers. For example, (3, 4, 5) is a valid Pythagorean triple
        because 3 * 3 + 4 * 4 = 5 * 5 or 9 + 16 = 25. There are an infinite number of such
        triples. For example, (5, 12, 13), (6, 8, 10), and (7, 24, 25) are all valid Pythagorean
        triples. Such triples are useful because they describe the three side lengths of a
        right-angled triangle.

        REPRESENTING A TRIPLE
        Where do you start? The first step is to define a triple. Instead of (more properly)
        defining a new class to represent a triple, you can use an array of int with three
        elements. For example, new int[]{3, 4, 5} to represent the tuple (3, 4, 5). You can now
        access each individual component of the tuple using array indexing.

        FILTERING GOOD COMBINATIONS
        Let’s assume someone provides you with the first two numbers of the triple: a and b.
        How do you know whether that will form a good combination? You need to test whether
        the square root of a * a + b * b is a whole number. This is expressed in Java as Math
        .sqrt(a*a + b*b) % 1 == 0. (Given a floating-point number, x, in Java its fractional part
        is obtained using x % 1.0, and whole numbers like 5.0 have zero fractional part.) Our code
        uses this idea in a filter operation (you’ll see how to use this later to form valid code):
             filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)

        Assuming that surrounding code has given a value for a, and assuming stream provides
        possible values for b, filter will select only those values for b that can form a
        Pythagorean triple with a.

        GENERATING TUPLES
        Following the filter, you know that both a and b can form a correct combination.
        You now need to create a triple. You can use the map operation to transform each element
        into a Pythagorean triple as follows:
                stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                    .map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});

        GENERATING B VALUES
        You’re getting closer! You now need to generate values for b. You saw that Stream
        .rangeClosed allows you to generate a stream of numbers in a given interval. You can
        use it to provide numeric values for b, here 1 to 100:
                IntStream.rangeClosed(1, 100)
                    .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                    .boxed()
                    .map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
        Note that you call boxed after the filter to generate a Stream<Integer> from the
        IntStream returned by rangeClosed. This is because map returns an array of int for
        each element of the stream. The map method from an IntStream expects only
        another int to be returned for each element of the stream, which isn’t what you want!
        You can rewrite this using the method mapToObj of an IntStream, which returns an
        object-valued stream:
                IntStream.rangeClosed(1, 100)
                    .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                    .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});

        GENERATING A VALUES
        There’s one crucial component that we assumed was given: the value for a. You now
        have a stream that produces Pythagorean triples provided the value a is known. How
        can you fix this? Just like with b, you need to generate numeric values for a! The final
        solution is as follows:

         */
        Stream<int[]> pythagoreanTriples =
                IntStream.rangeClosed(1, 100).boxed()
                    .flatMap(a ->
                        IntStream.rangeClosed(a, 100)
                                .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                                .mapToObj(b ->
                                        new int[]{a, b, (int) Math.sqrt(a*a + b*b)}));
        /*
        Ok, what's the flatMap about? First, you create a numeric range from 1 to 100 to generate
        values for a. For each given value of a you're creating a stream of triples. Mapping a
        value of a to a stream of triples would result in stream of streams! The flatMap method
        does the mapping and also flattens all the generated streams of triples into a single
        stream. As a result, you produce a stream of triples. Note also that you change the range
        of b to be a to 100. There's no need to start the range at the value 1 because this would
        create duplicate triples (for example (3,4,5) and (4,3,5)).
         */
        System.out.println();
        pythagoreanTriples.limit(5).forEach(t ->
                System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

        // Can You Do Better?
        /*
        The current solution isn't optimal because you calculate the square root twice. Once
        possible way to make your code more compact is to generate all triples of the form
        (a*a, b*b, a*a+b*b) and then filter the ones that match your criteria:
         */

        Stream<double[]> pythagoreanTriples2 =
                IntStream.rangeClosed(1, 100).boxed()
                    .flatMap(a ->
                            IntStream.rangeClosed(a, 100)
                                    .mapToObj(b -> new double[]{a, b, Math.sqrt(a*a + b*b)})
                                    .filter(t -> t[2] % 1 == 0));
        pythagoreanTriples2.limit(5).forEach(t ->
                System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
    }
}
