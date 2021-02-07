package com.besimgurbuz.refactoringTestingAndDebugging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebuggingLambda {

    public static List<Integer> numbers = Arrays.asList(2, 3, 4, 5);
    /*
    Logging information
    Suppose that you're trying to debug a pipeline of operations in a stream. What can you do? You could use forEach
    to print or log the result of a stream as follows:
     */
    static {
        numbers.stream()
                .map(x -> x + 17)
                .filter(x -> x % 2 == 0)
                .limit(3)
                .forEach(System.out::println);
    }
    /*
    This code produces the following output:
    20
    22

    Unfortunately, after you call forEach, the whole stream is consumed. It would be useful to understand what each
    operation (map, filter, limit) produces in the pipeline of a stream.

    The stream operation peek can help. The purpose of peek is to execute an action on each element of a stream as
    it's consumed. It doesn't consume the whole stream the way forEach does, however; it forwards the element on
    which it performed an action to the next operation in the pipeline.
     */
    public static void main(String[] args) {
        List<Integer> result =
               numbers.stream()
                       .peek(x -> System.out.println("from stream: " + x))
                       .map(x -> x + 17)
                       .peek(x -> System.out.println("after map: " + x))
                       .filter(x -> x % 2 == 0)
                       .peek(x -> System.out.println("after filter: " + x))
                       .limit(3)
                       .peek(x -> System.out.println("after limit: " + x))
                       .collect(Collectors.toList());
    }
}
