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

        {OTHER=[french fries, pizza], MEAT=[pork, beef], FISH=[]}
         */

        /*
        Another even more common way in which it could be useful to manipulate the grouped elements
        is transforming them through a mapping function. To this purpose, similarly to what you
        have seen for the filtering Collector, the Collectors class provides another Collector
        through the mapping method that accepts a mapping function and another Collector used to
        gather the elements resulting from the application of that function to each of them. By
        using it you can, for instance, convert each Dish in the groups into their respective
        names in this way:
         */

        Map<Dish.Type, List<String>> dishNamesByType =
                menu.stream()
                    .collect(groupingBy(Dish::getType,
                            mapping(Dish::getName, toList())));
        System.out.println(dishNamesByType);

        /*
        Note that in this case each group in the resulting Map is a List of Strings rather than
        one of Dishes as it was in the former examples. YOu could also use a third Collector in
        combination with the groupingBy to perform a flatMap transformation instead of a plain
        map. To demonstrate how this works let's suppose that we have a Map associating to each
        Dish a list of tags it follows:
         */

        Map<String, List<String>> dishTags = new HashMap<>();

        dishTags.put("pork", Arrays.asList("greasy", "salty"));
        dishTags.put("beef", Arrays.asList("salty", "roasted"));
        dishTags.put("chicken", Arrays.asList("fried", "crisp"));
        dishTags.put("french fries", Arrays.asList("greasy;", "fried"));
        dishTags.put("rice", Arrays.asList("light", "natural"));
        dishTags.put("season fruit", Arrays.asList("fresh", "natural"));
        dishTags.put("pizza", Arrays.asList("tasty", "salty"));
        dishTags.put("prawns", Arrays.asList("tasty", "roasted"));
        dishTags.put("salmon", Arrays.asList("delicious", "fresh"));

        /*
        In case you are required to extract these tags fro each group of type of dishes you can
        easily achieve this using the flatMapping Collector:
         */
        Map<Dish.Type, Set<String>> dishNamesByType2 =
                menu.stream()
                    .collect(groupingBy(Dish::getType,
                            flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
        System.out.println(dishNamesByType2);
        /*
        Here for each Dish we are obtaining a List of tags. So analogously to what we have already
        seen in the former chapter, we need to perform a flatMap in order to flatten the resulting
        two-level list into a single one. Also note that this time we collected the result of the
        flatMapping operations executed in each group into a Set instead of using a List as we did
        before, in order to avoid repetitions of some tags associated to more than one Dish in the
        same type. The Map resulting from this operation is then the following:

        {OTHER=[salty, natural, light, greasy;, tasty, fresh, fried],
            MEAT=[salty, greasy, roasted, fried, crisp], FISH=[roasted, tasty, fresh, delicious]}

        Until this point we only used a single criterion to group the dishes in the menu, for
        instance their type or by calories, but what if you want ot use more than one criterion at
        the same time? Grouping is powerful because it composes effectively. Let's see how to do
        this.
         */

        // Multilevel grouping
        /*
        The two arguments Collectors.groupingBy factory method that we used in a former section to
        manipulate the elements in the groups resulting from the grouping operation can be used
        also to perform a two-level grouping. To achieve this you can pass to it a second inner
        groupingBy to the outer groupingBy,, defining a second-level criterion to classify the
        stream's items, as shown in the next listing.
         */
        Map<Dish.Type, Map<CaloricLevel, List<String>>> dishesByTypeCaloricLevel =
        menu.stream().collect(
                groupingBy(Dish::getType,
                        groupingBy(dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }, mapping(Dish::getName, toList()))
                )
        );

        System.out.println(dishesByTypeCaloricLevel);

        /*
        In general, it helps to think that groupingBy works in terms of "buckets." The first
        groupingBy creates a bucket for each hey. You then collect he elements in each bucket with
        the downstream collector and so on to achieve n-level groupings!
         */
        // Collecting data in subgroups
        /*
        In the previous example, we saw that's possible to pass a second groupingBy collector to
        the outer one to achieve a multilevel grouping. But more generally, the second collector
        passed to the first groupingBy can be any type of collector, not just another groupingBy.
        For instance, it's possible to count the number of Dishes in the menu for each type, by
        passing the counting collector as a second argument to the groupingBy collector:
         */
        Map<Dish.Type, Long> typeCount = menu.stream().collect(
                groupingBy(Dish::getType, counting()));

        System.out.println(typeCount);

        /* ****
        Also note that the regular one-argument gropingBy(f), where f is the classification
        function is, in reality, shorthand for gropingBy(f, toList()).

        To give another example, you could rework the collector you already used to find the
        highest-calorie dish in the menu to achieve a similar result, but now classified by
        the type of dish:
         */

        Map<Dish.Type, Optional<Dish>> result = menu.stream().collect(groupingBy(
                Dish::getType,
                maxBy(Comparator.comparingInt(Dish::getCalories))));
        System.out.println(result);

        /*
        NOTE The values in this Map are Optionals because this is the resulting type of the
        collector generated by the maxBy factory method, but in reality if there's no Dish in the
        menu for a given type, that type won't have an Optional.empty() as value; it won't be
        present at all as a key in the Map. The groupingBy collector lazily adds a new key in the
        grouping Map only the first time it finds an element in the stream, producing that key
        when applying on it the grouping criteria being used. This means that in this case, the
        Optional wrapper isn't useful, because it's not modeling a value that could be possibly
        absent but is there incidentally, only because this is the type returned by the reducing
        collector.
         */

        // ADAPTING THE COLLECTOR RESULT TO A DIFFERENT TYPE
        /*
        Because the Optionals wrapping all the values in the Map resulting from the last grouping
        operation aren't useful in this case, you may want to get rid of them. To achieve this, or
        more generally, to adapt the result returned by a collector to different type, you could
        use the collector returned by the Collectors.collectingAndThen factory method, as shown
        in the following listing.
         */
        Map<Dish.Type, Dish> mostCaloricByType =
                menu.stream()
                    .collect(groupingBy(Dish::getType,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(Dish::getCalories)),
                                        Optional::get)));
        System.out.println(mostCaloricByType);
        /*
        This factory method takes two arguments-the collector to be adapted and a transformation
        function-and returns another collector. This additional collector acts as a wrapper for
        the old one and maps the value it returns using the transformation function as the last
        step of the collect operation. In this case, the wrapper collector is the one created with
        maxBy, and the transformation function, Optional::get, extracts the value contained in the
        Optional returned. As we've said, here this is safe because the reducing collector will
        never return an Optional.empty(). The result is the following Map:

            {FISH=salmon, OTHER=pizza, MEAT=pork}
         */

        // OTHER EXAMPLES OF COLLECTORS USED IN CONJUNCTION WITH GROUPINGBY
        /*
        More generally, the collector passed as second argument to the groupingBy factory method
        will be used to perform a further reduction operation on all the elements in the stream
        classified into the same group. For example, you could also reuse the collector created to
        sum the calories of all the dishes in the menu to obtain a similar result, but this time
        for each group of Dishes:
         */

        Map<Dish.Type, Integer> totalCaloriesByType =
                menu.stream().collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));

        System.out.println(totalCaloriesByType);

        /*
        Yet another collector, commonly used in conjunction with groupingBy, is one generated by
        the mapping method. This method takes two arguments: a function transforming the elements
        in a stream and a further collector accumulating the objects resulting from this
        transformation. Its purpose is to adapt a collector accepting elements of given type to
        one working on objects of a different type, by applying a mapping function to each input
        element before accumulating them. To see practical example of using this collector,
        suppose you want to know which CaloricLevels are available in the menu for each type of
        Dish. You could achieve this result combining a groupingBy and a mapping collector, as
        follows:
         */

        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
                menu.stream().collect(groupingBy(Dish::getType,
                        mapping(dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return  CaloricLevel.FAT;
                        }, toSet())));

        System.out.println(caloricLevelsByType);

        /*
        Here the transformation function passed to the mapping method maps a Dish into its
        CaloricLevel, as you've seen before. The resulting stream of CaloricLevels is then passed
        to a toSet collector, analogous to the toList one, but accumulating the elements of a
        stream into a Set instead of into a List, to keep only the distinct values. As in earlier
        examples, this mapping collector will then be used to collect the elements in each
        substream generated by the grouping function, allowing you to obtain as a result the
        following Map:

           {OTHER=[DIET, NORMAL], MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL]}

        From this you can easily figure out your choices. If you're in the mood for fish and
        you're on a diet, you could easily find a dish; likewise, if you're hungry and want
        something with lots fo calories, you could satisfy your robust appetite by choosing
        something from the meat section of the menu. Note that in the previous example, there are
        no guarantees about what type of Set is returned. But using toCollection, you can have
        more control. For example, you can ask for a HashSet by passing a constructor reference to
        it:
         */

        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType2 =
                menu.stream().collect(
                        groupingBy(Dish::getType,
                                mapping(dish -> {
                                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                    else return CaloricLevel.FAT;
                                }, toCollection(HashSet::new))
                        )
                );
        System.out.println(caloricLevelsByType2);
    }
    public enum CaloricLevel {DIET, NORMAL, FAT}
}