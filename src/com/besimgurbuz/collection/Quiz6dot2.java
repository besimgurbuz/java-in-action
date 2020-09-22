package com.besimgurbuz.collection;

import com.besimgurbuz.models.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
/**
 * @author Besim Gurbuz
 */
public class Quiz6dot2 {

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


        // Using partitioningBy
        /*
        As you've seen, like the groupingBy collector, the partitioningBy collector can be used in
        combination with other collectors. In particular it could be used with a second
        partitioningBy collector to achieve a multilevel partitioning. What will be the result of
        the following multilevel partitioning?
         */

        Map<Boolean, Map<Boolean, List<Dish>>> partitioning1 = menu.stream().collect(
                partitioningBy(Dish::isVegetarian, partitioningBy(d -> d.getCalories() > 500)));

        System.out.println(partitioning1);

//        menu.stream().collect(partitioningBy(Dish::isVegetarian, partitioningBy(Dish::getType)));

        Map<Boolean, Long> partitioning2 = menu.stream().collect(
                partitioningBy(Dish::isVegetarian, counting()));

        System.out.println(partitioning2);
    }

}
