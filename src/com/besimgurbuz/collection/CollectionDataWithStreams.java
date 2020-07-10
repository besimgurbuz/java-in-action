package com.besimgurbuz.collection;

import com.besimgurbuz.models.Dish;
import com.besimgurbuz.models.Trader;
import com.besimgurbuz.models.Transaction;

import java.util.*;

import static java.util.stream.Collectors.*;
/**
 * @author Besim Gurbuz
 */
public class CollectionDataWithStreams {
    /*
    Here are some example queries of what you'll be able to do using collect and collectors:
        * Group a list o transactions by currency to obtain the sum of the values of all
        transactions with that currency (returning Map<Boolean, List<Transaction>>)
        * Partition a list of transactions into two groups: expensive and not expensive (returning
        a Map<Boolean, List<Transaction>>)
        * Create multilevel groupings, such as grouping transactions by cities and then further
        categorizing by whether they're expensive or not (returning a Map<String, Map<Boolean,
        List<Transaction>>>)
     */


    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 950),
                new Transaction(raoul, 2011, 300),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 710),
                new Transaction(alan, 2012, 950)
        );

        Map<Integer, List<Transaction>> transactionsByAmount =
                transactions.stream().collect(groupingBy(Transaction::getAmount));

        System.out.println(transactionsByAmount);

        // Predefined collectors
        /*
        We'll mainly explore the features of the predefined collectors, those that can be created
        from the factory methods (such as gropingBy) provided by the Collectors class. These offer
        three main functionalities:

            * Reducing and summarizing stream elements to a single value
            * Grouping elements
            * Partitioning elements

         Reducing and summarizing
         To illustrate the range of possible collector instances that can be created from the
         Collectors factory class, we'll reuse the domain we introduced in the previous chapter:
         a menu consisting of a list of delicious dishes!

         As you learned, collectors (the parameters to the stream method collect) are typically used
         in cases where it's necessary to reorganize the stream's items into a collection. But more
         generally, they can be used every time you want to combine all the items in the stream
         into a single result. This result can be of any type, as complex as a multilevel map
         representing a tree or as simple as a single integer, perhaps representing the sum of all
         the calories in the menu.
         */
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
        // As a first simple example, let's count the number of dishes in the menu, using the collector returned by the counting factory method:
        long howManyDishes = menu.stream().collect(counting());

        // You can write this far more directly as
        long howManyDishes2 = menu.stream().count();

        /*
        but the counting collector can be useful when used in combination with other collectors, as
        we'll demonstrate later.
         */

        // finding maximum and minimum in a stream of values
        /*
        Suppose you want to find the highest-calorie dish in the menu. You can use two collectors,
        Collectors.maxBy and Collectors.minBy, to calculate the maximum or minimum value in a
        stream. These two collectors take aComparator as argument to compare the elements in the
        stream. Here you create a Comparator comparing dishes base on their calorie content and
        pass it to Collectors.maxBy:
         */
        Comparator<Dish> dishCaloriesComparator =
                Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish =
                menu.stream().collect(maxBy(dishCaloriesComparator));

        mostCalorieDish.ifPresent(dish -> System.out.println("Dish is " + dish.getName()));

        double avgCalories = menu.stream().collect(averagingDouble(Dish::getCalories));

        int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
        System.out.println("Total Calories: " + totalCalories);
        System.out.println("Average Calorie is: " + avgCalories);
        IntSummaryStatistics menuStatistics =
                menu.stream().collect(summarizingInt(Dish::getCalories));
        System.out.println(menuStatistics);

        /*
        This collector gathers all that information in a class called IntSummaryStatistics that
        provides convenient getter methods to access the results.

        As usual, there are corresponding summarizingLong and summarizingDouble factory methods
        with associated types LongSummaryStatistics and DoubleSummaryStatistics. These are used
        when the property to be collected is a primitive-type long or a double.
         */
        // Joining Strings
        /*
        The collectors returned by the *joining* factory method concatenates into a single string,
        all strings resulting from invoking the toString method on each object in the stream.
        This means you can concatenate the names of all the dish;;es in the menu as follows:
         */
        String shortMenu = menu.stream().map(Dish::getName).collect(joining(" "));
        System.out.println(shortMenu);

        /*
        Note that *joining* internally makes use of a StringBuilder to append the generated strings
        into one. Also note that if the Dish class had a toString method returning t;he dish's
        name, you'd obtain the same result without needing to map over the original stream with a
        function extracting the name from each dish:
         */
        // String shortMenu2 = menu.stream().collect(joining());

        /*
        Generalized summarization with reduction

        All the collectors we've discussed so far are, in reality, only convenient specializations
        of a reduction process that can be defined using the reducing factory method. The
        Collectors.reducing factory method is a generalization of all of them. The special cases
        discussed earlier are arguably provided only for programmer convenience. (But remember that
        programmer convenience and readability are of prime importance!) For instance, it's
        possible to calculate the total calories in your menu with a collector created from the
        reducing method as follows:
         */
        int totalCaloriesReducing = menu.stream().collect(reducing(
                0, Dish::getCalories, (i, j) -> i + j));

        System.out.println("Total calorie calculated with reduce: " + totalCaloriesReducing);
        /*
        It takes three arguments:
            * The first argument is the staring value of the reduction operation and will also be
            the value returned in the case of a stream with no elements, so clearly 0 is the
            appropriate value in the case of numeric sum.
            * The second argument transform a dish into an int representing its calorie content.
            * The third argument is a BinaryOperator that aggregates two items into a single value
            of the same type. Here, it su;ms two ints.

        Similarly, you could find the highest-calorie dish using the one-argument version of
        reducing as follows:
         */
        Optional<Dish> mostCalorieDishReducing =
                menu.stream().collect(reducing((d1, d2) ->
                        d1.getCalories() > d2.getCalories() ? d1 : d2));
        mostCalorieDishReducing.ifPresent(dish -> System.out.println(dish.getName()));

        /*
        You can think of the collector created with the one-argument reducing factory method as a
        particular case of the three-argument method, which uses the first item in the stream as
        a starting point and an identity function (a function that returns its input argument as
        is) as a transformation function. This also implies that the one-argument *reducing*
        collector won't have any starting point when passed to the collect method of an empty
        stream and, it returns Optional<Dish> object.
         */

        int totalCalories2 = menu.stream()
                .map(Dish::getCalories).reduce(Integer::sum).get();
        // i think this is better for totalCalories
        int totalCalories3 = menu.stream().mapToInt(Dish::getCalories).sum();

        /*
        Grouping

        A common database operation is to group items in a set, based on one or more properties.
        As you saw in the earlier transactions-currency-grouping example, this operation can be
        cumbersome, verbose, and error-prone when implemented with an imperative style. But it can
        be easily translated in a single, readable statement by rewriting it in a ore functional
        style as encouraged by Java 8. As a second example of how this feature works, suppose you
        want to classify the dishes in the menu according to their type, putting the ones
        containing meat in a group, the ones with fish in another group, and all others in a third
        group. You can easily perform this task using a collector returned by the
        Collectors.groupingBy factory method, as follows:
         */
        Map<Dish.Type, List<Dish>> dishesByType = menu.stream()
                .collect(groupingBy(Dish::getType));
        System.out.println(dishesByType);

        Map<CaloricLevel, List<Dish>> dishedByCaloricLevel = menu.stream().collect(
                groupingBy(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                })
        );

        System.out.println(dishedByCaloricLevel);

        /*
        Manipulating Grouped elements

        Frequently after performing a grouping operation you may need to manipulate the elements
        in each resulting group. Suppose, for example, that you want to filter only the caloric
        dishes, let's say the ones with more than 500 calories. You may argue that in this case
        you could apply this filtering predicate before the grouping like the following:
         */
        Map<Dish.Type, List<Dish>> caloricDishesByType =
                menu.stream().filter(dish -> dish.getCalories() > 500)
                        .collect(groupingBy(Dish::getType));

        System.out.println(caloricDishesByType);

        /*
        Do you see the problem there? Because there is no dish of type FISH satisfying our
        filtering predicate, that key totally disappeared from the resulting map. To workaround
        this problem Collectors class overloads the groupingBy factory method, with one variant
        also taking a second argument of type Collector along with the usual classification
        function. In this way, it's possible to move the filtering predicate inside this second
        Collector, as follows:
         */
        Map<Dish.Type, List<Dish>> caloricDishesByType2 =
                menu.stream()
                        .collect(groupingBy(Dish::getType,
                                filtering(dish -> dish.getCalories() > 500, toList())));
        System.out.println(caloricDishesByType2);
        /*
        The filtering method is another static factory method of the Collectors class accepting a
        Predicate to filter the elements in each group and further Collector that is used to
        regroup the filtered elements. In this way, the resulting Map will also keep an entry for
        the FISH type even if it maps an empty List:
         */
    }
    public enum CaloricLevel {DIET, NORMAL, FAT}
}