package com.besimgurbuz.streams;

import com.besimgurbuz.models.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

/**
 * @author Besim Gurbuz
 */
public class StreamPipeline {
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
        System.out.println(names);
        /*
        By doing this, you can notice that the Streams library performs several optimizations exploiting the lazy
        nature of streams. First, despite the fact that many dishes have more than 300 calories, only the first three
        are selected! This is because of the limit operation and a technique called short-circuiting, as we’ll explain
        in the next chapter. Second, despite the fact that filter and map are two separate operations, they were merged
        into the same pass (compiler experts call this technique loop fusion).
         */
    }
}
