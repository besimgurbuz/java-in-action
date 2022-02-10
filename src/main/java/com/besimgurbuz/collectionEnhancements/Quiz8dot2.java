package com.besimgurbuz.collectionEnhancements;

import org.openjdk.jmh.generators.core.BenchmarkGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class Quiz8dot2 {
    /*
    Figure out what the following code does, and think of what idiomatic operation you could use to
    simplify what it’s doing:
     */
    public static void main(String[] args) {
        Map<String, Integer> movies = new HashMap<>();
        movies.put("JamesBond", 20);
        movies.put("Matrix", 15);
        movies.put("Harry Potter", 5);
        Map<String, Integer> moviesCopy = new HashMap<>(movies);

        // Answer
        movies.entrySet().removeIf(entry -> entry.getValue() < 10);

        System.out.println(movies);

        // With streams
        moviesCopy = moviesCopy.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue() > 10)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(moviesCopy);

    }
}
