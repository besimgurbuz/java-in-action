package com.besimgurbuz.collection;

import com.besimgurbuz.models.Dish;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

/**
 * @author Besim Gurbuz
 */
public class CollectionPartitioning {
    /*
    Partitioning is a special case of grouping: having a predicate called a partitioning function
    as a classification function. The fact that the partitioning function returns a boolean means
    the resulting grouping Map will have a Boolean as a key type, and therefore, there can be at
    most two different groups-one for true and one for false. For instance, if you're vegetarian of
    have invited a vegetarian friend to have dinner with you, you may be interested in partitioning
    the menu into vegetarian and non-vegetarian dishes:
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

        Map<Boolean, List<Dish>> partitionedMenu =
                menu.stream().collect(partitioningBy(Dish::isVegetarian));

        System.out.println(partitionedMenu);

        /*
        This will return the following Map:

        {false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit,
        pizza]}

        So you could retrieve all the vegetarian dishes by getting from this Map the value indexed
        with the key true:

         */

        List<Dish> vegetarianMenu = partitionedMenu.get(true);
        System.out.println(vegetarianMenu);

        /*
        Note that you could achieve the same res;ult by filtering the stream created fromthe menu
        List with the same predicate used for partitioning and then collecting the result in an
        additional List:
            List<Dish> vegetarianDishes =
                menu.stream().filter(Dish::isVegetarian).collect(toList());
         */

        // Advantages of partitioning
        /*
        Partitioning has the advantage of keeping both lists of the stream elements, for which the
        application of the partitioning function returns true or false. In the previous example,
        you can obtain the List of the non-vegetarian Dishes by accessing the value of the key
        false in the partitionedMenu Map, using two separate filtering operations: one with the
        predicate and one with its negation. Also, as you already saw for grouping, the
        partitioningBy factory method has an overloaded version to which you can pass a second
        collector, as shown here:
         */

        Map<Boolean, Map<Dish.Type, List<String>>> vegetarianDishNamesByType =
                menu.stream().collect(
                        partitioningBy(Dish::isVegetarian,
                                groupingBy(Dish::getType, mapping(Dish::getName, toList()))));

        System.out.println(vegetarianDishNamesByType);

        /*
        This will produce a two-level Map:

        {false={FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, true={OTHER=[french fries,
        rice, season fruit, pizza]}}

        Here the grouping of the dishes by their type is applied individually to both of the
        sub-stream of vegetarian and non-vegetarian dishes resulting from the partitioning,
        producing a two-level Map that's similar to the one you obtained when you performed the
        two-level grouping in section 6.3.1. As another example, you can reuse your earlier code to
        find the most caloric dish among both vegetarian and non-vegetarian dishes:
         */

        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =
                menu.stream().collect(
                        partitioningBy(Dish::isVegetarian,
                                collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories))
                                        , Optional::get))
                );
        System.out.println(mostCaloricPartitionedByVegetarian);
        // That will produce the following result:
        // {false=pork, true=pizza}

        /*
        We started this section by saying that you can think of partitioning as a special case of
        grouping. It's worth also nothing that the Map implementation returned by partitioningBy
        is more compact and efficient as it only needs to contain two keys: true and false. In
        fact, the internal implementation is a specialized Map with two fields. The analogies
        between the groupingBy and partitioningBy collectors don't end here; as you'll see in the
        next quiz, you can also perform multilevel partitioning in a way similar to what you did
        for grouping.
         */

        // partitioning numbers into prime and non-prime.
        IntStream.range(0,100).
    }
}
