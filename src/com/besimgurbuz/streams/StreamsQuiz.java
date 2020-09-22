package com.besimgurbuz.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class StreamsQuiz {
    /*
    Quiz 5.2: Mapping
    1. Given a list of numbers, how would you return a list of the square of each number?
    For example, given [1, 2, 3, 4, 5] you should return [1, 4, 9, 16, 25].
     */
    static List<Integer> quiz52(List<Integer> numbers) {
        return numbers.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
    }

    static List<int[]> quiz52_2(List<Integer> firstList, List<Integer> secondList) {
        return firstList.stream()
                .flatMap(i -> secondList
                        .stream().map(j -> new int[]{i, j}))
                .collect(Collectors.toList());
    }

    static List<List<Integer>> quiz52_3(List<Integer> list1, List<Integer> list2) {
        return list1.stream()
                .flatMap(i -> list2.stream().map(j -> Arrays.asList(i, j)))
                .filter(l -> (l.get(0) + l.get(1)) % 3 == 0)
                .collect(Collectors.toList());
    }

    // BOOKS QUIZ52_3 SOLUTION:
    List<Integer> numbers1 = Arrays.asList(1, 2, 3);
    List<Integer> numbers2 = Arrays.asList(3, 4);
    List<int[]> pairs = numbers1.stream()
            .flatMap(i -> numbers2.stream()
                            .filter(j -> (i + j) % 3 == 0)
                            .map(j -> new int[] {i, j}))
                            .collect(Collectors.toList());

    public static void main(String[] args) {
        List<int[]> pairs = quiz52_2(Arrays.asList(1,2,3,4), Arrays.asList(1, 4));
        pairs.forEach(ints -> {
            Arrays.stream(ints).forEach(System.out::print);
            System.out.print("-");
        });

        List<List<Integer>> pairsDevidesBy3 = quiz52_3(Arrays.asList(1, 2, 3, 4),
                Arrays.asList(1, 5));

        System.out.println();
        pairsDevidesBy3.forEach(intList -> {
            System.out.print("(");
            intList.forEach(System.out::print);
            System.out.println(")\n");
        });
    }
}
