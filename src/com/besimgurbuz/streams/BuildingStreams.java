package com.besimgurbuz.streams;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.IntSupplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class BuildingStreams {

    public static void main(String[] args) {
        /*
        You were able to get a stream from a collection using the stream method. In addition, we
        showed you how to create numerical stream from a range numbers. But you can create streams
        in many more ways! This section shows how you can create a stream from a sequence of
        values, from an array, from a file, and even from a generative function to create infinite
        streams!

        Streams from Values
        You can create a stream with explicit values by using the static method Stream.of, which can
        take any number of parameters. For example, in the following code you create a stream of
        strings directly using Stream.of You then convert the strings to uppercase before printing
        them one by one:
        */
        Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);

        // You can get an empty stream using the empty method as follows:
        Stream<String> emptyStream = Stream.empty();

        //Stream form nullable
        /*
        In Java 9, a new method was added that lets you create a stream from a nullable object.
        After playing with streams, you may have encountered a situation where you extracted an
        object that may be null and then needs to be converted into a stream (or an empty stream
        of null). For example, the method System.getProperty returns null if there is no property
        with the given key. To use it together with a stream, you'd need to explicitly check for
        null as follows:
         */

        String homeValue = System.getProperty("home");
        Stream<String> homeValueStream = homeValue == null ? Stream.empty() : Stream.of(homeValue);

        // Using Stream.ofNullable you can rewrite this code more simply:
        Stream<String> homeValueStream2 = Stream.ofNullable(System.getProperty("home"));

        // This pattern can be particularly handy in conjunction with flatMap and a stream of values
        // that may include nullable objects.
        Stream<String> values =
                Stream.of("config", "home", "user")
                    .flatMap(key -> Stream.ofNullable(System.getProperty(key)));

        // Streams from arrays
        /*
        You can create a stream from an array using the static method Arrays.stream, which takes an
        array as parameter. For example, you can convert an array of primitive ints into an
        IntStream and then sum the IntStream to produce an int, as follows:
         */

        int[] numbers = {2, 3, 5, 7, 11, 13};
        int sum = Arrays.stream(numbers).sum();
        System.out.println(sum);

        // Streams from files
        /*
        Java's NIO API (non-blocking I/O), which is used for I/O operations such as processing a
        file, has been updated to take advantage of the Streams API. Many static methods in
        java.nio.file.Files return a stream. For example, a useful method is Files.lines, which
        returns a stream of lines as strings from a given file. Using what you've learned so far,
        you could use this method to find out the number of unique words is a file follows:
         */

        long uniqueWords = 0;
        try(Stream<String> lines =
                    Files.lines(Paths.get("src/com/besimgurbuz/streams/file.txt"), Charset.defaultCharset())) {
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                    .distinct()
                    .count();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(uniqueWords);
        /*
        You use Files.lines to return a stream where each element is a line in the given file. This
        call is surrounded by a try/catch block because the source if the stream is an I/O
        resource.
        In fact, the call Files.lines will open an I/O resource, which needs to be closed to avoid
        leak. In the past, you'd need an explicit finally block to do this. Conveniently, the
        Stream interface implements the interface AutoCloseable. This means that the management of
        the the resource is handled for you within the try block. Once you have a stream of lines,
        you can then split each line into words by calling the split method on line. Notice how
        you use *flatMap* to produce one flattened stream of words instead of multiple streams of
        words for each line. Finally, you count each distinct word in the stream by chaining the
        methods distinct and count.
         */

        // Streams from functions: Creating infinite streams!
        /*
        The Streams API provides two static methods to generate a stream from a function:
        Stream.iterate and Stream.generate. These two operations let you create what we call an
        *infinite stream*, a stream that doesn't have a fixed size like when you create a stream
        from a fixed collection. Streams produced by *iterate* and *generate* create values on
        demand given function and can therefore calculate values forever! It's generally sensible
        to use limit(n) on such streams to avoid printing an infinite number of values.

        Iterate
        Let's look at simple example of how to use iterate before we explain it:
         */
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);
        /*
        The iterate method takes an initial value, here 0, and a lambda (of type UnaryOperator<T>)
        to apply successively on each new value produced. Here you return the previous element
        added with 2 using the lambda n -> n + 2. As a result, the iterate method produces a stream
        of all even numbers: the first element of the stream is the initial value 0. Then it adds
        2 to produce the new value 2; it adds 2 again and produce the new value 4 an so on. This
        iterate operation is fundamentally sequential because the result depends on the previous
        application. Note that this operation produces an infinite stream-the stream doesn't have
        an end because values are computed on demand and can be computed forever. We say the stream
        is unbounded. As we discussed earlier, this is a key difference between a stream and a
        collection. You're using the limit method to explicitly limit the size of the stream. Here
        you select only the first 10 even numbers. You then call the forEach terminal operation to
        consume the stream and print each element individually.

        In general, you should use iterate when you need to produce a sequence of successive values
        (for example, a date followed by its next date: January 31, February 1, and so on).
         */

        // Fibonacci
        Stream.iterate(new int[]{0, 1}, v -> new int[] { v[1],  v[0] + v[1]})
                .limit(20)
                .forEach(t -> System.out.println("(" + t[0] + "," + t[1] + ")"));

        /*
        In Java 9, the iterate method was enhanced with support for a predicate. For example, you
        can generate numbers starting at 0 but stop the iteration once the number is greater than
        100:
         */
        IntStream.iterate(0, n -> n < 100, n -> n + 4)
                .forEach(System.out::println);
        /*
        The iterate method takes a predicate as its second argument that tells you when to continue
        iterating up until. Note that you may think that you can use the filter operation to
        achieve the same result:

            IntStream.iterate(0, n -> n + 4)
                    .filter(n -> n < 100)
                    .forEach(System.out::println);

        Unfortunately that isn't the case. In fact, this code wouldn't terminate! The reason is
        that there's no way to know in the filter that numbers continue to increase, so it keeps
        on filtering them infinitely! You could solve the problem by using takeWhile, which would
        short-circuit the stream:
        */
        IntStream.iterate(0, n -> n + 4)
                .takeWhile(n -> n < 100)
                .forEach(System.out::println);

        // Generate
        /*
        Similarly to method iterate, the method generate lets you produce an infinite stream of
        values computed on demand. But generate doesn't apply successively a function on each new
        produced value. It takes a lambda of type Supplier<T> to provide new values. Let's look at
        an example how to use it:
         */
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

        DoubleStream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);
        /*
        The static method Math.random is used as generator for new values. Again you limit the size
        of the stream explicitly using the limit method; otherwise the stream would be unbounded!

        You may be wondering whether there's anything else useful you can do using the method
        generate. The supplier we used (a method reference to Math.random) was stateless: is
        wasn't recording any values somewhere that can be used in later computations. But a
        supplier doesn't have to be stateless. You can create a supplier that stores state that it
        can modify and use when generating the next value of the stream.

        But it's important to note that a supplier that's stateful isn't safe to use parallel code.
        The stateful IntSupplier for Fibonacci is shown at the end of this chapter for completeness
        but should generally be avoided!

        We'll use an IntStream in our example to illustrate code that's designed to avoid boxing
        operations. The generate method on IntStream takes an IntSupplier instead of a Supplier<T>
        For example, here's how to generate an infinite stream of ones:
         */
        IntStream ones = IntStream.generate(() -> 1);

        /*
        To come back to our Fibonacci tasks, what you need to do now is create an Int-Supplier
        that maintains in its state the previous value in the series, so getAsInt can use it to
        calculate the next element. In addition, it can update the state of the IntSupplierfor the
        next time it’s called. The following code shows how to create an IntSupplierthat will
        return the next Fibonacci element when it’s called:
         */
        IntSupplier fib = new IntSupplier() {
            private int previous = 0;
            private int current = 1;
            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrevious;
            }
        };

        /*
        The code creates an instance of IntSupplier. This object has a mutable state: it tracks the
        previous Fibonacci element and the current Fibonacci element in two instance variables.
        The getAsInt method changes the state of the object when it's called so was purely
        immutable; you didn't modify existing state but were creating new tuples at each
        iteration. You'll learn in chapter 7 that you should always prefer an immutable approach
        in order to precess a stream in parallel an expect a correct result.

        Note that because you're dealing with a stream of infinite size, you have to limit its
        size explicitly using the operation limit; otherwise, the terminal operation (in this case
        forEach) will compute forever. Similarly, you can't sort or reduce an infinite stream
        because all elements need to be precessed, but this would take forever because the stream
        contains an infinite number of elements!
         */

        IntStream.generate(fib).limit(10).forEach(System.out::println);
    }
}
