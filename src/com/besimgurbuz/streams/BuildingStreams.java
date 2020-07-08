package com.besimgurbuz.streams;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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
        }
        catch (IOException e) {
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

    }
}
