package com.besimgurbuz.streams;

import com.besimgurbuz.models.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class WorkingWithStreams {

    public static void main(String[] args) {
        /*
        Filtering unique elements
            Streams also support a method called distinct that returns a stream with unique elements
        (according to the implementation of the hashcode and equals methods of the objects produced
        by the stream). For example, the following code filter all even numbers from a list and
        then eliminates duplicates (using equals method for the comparison).
         */
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 4, 3, 5, 2, 1);
        numbers.stream()
                .filter(integer -> integer % 2 == 0)
                .distinct()
                .forEach(System.out::println);

        /*
        Slicing using a predicate
            Java 9 added two new methods that are useful for efficiently selecting elements in a
        stream: takeWhile and dropWhile.

        * Using takeWhile
        Lets say you have the following special of dishes:
         */
        List<Dish> specialMenu = Arrays.asList(
                new Dish("seasonal fruit", true, 120, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER)
        );
        /*
        How would you select the dish;es that ;have fewer than 320 calories? Instinctively, you
        know already from the previous section that the operation filter can be used as follows:
         */
        List<Dish> filteredMenu =
                specialMenu.stream()
                        .filter(dish -> dish.getCalories() < 320)
                        .collect(Collectors.toList());
        /*
        But, you'll notice that the initial list was already sorted on the number of calories! The
        downside of using the filter operations here is that you need to iterate through the whole
        stream and the predicate is applied to each element. Instead, you could stop once you found
        a dish that greater than (or equal to) 320 calories. With a small list this may not seem
        like a huge benefit, but it can become useful if you work with potentially large stream of
        elements. But how do you specify this? The `takeWhile` operation is here to rescue you! It
        lets you slice any stream using a predicate. Vit thankfully, it stops once it has found an
        element that fails to match. Here's how you can use it:
         */
        List<Dish> slicedMenu1 = specialMenu.stream()
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(Collectors.toList());

        slicedMenu1.forEach(dish -> System.out.println(dish.getName())); // list will have 2 values seasonal fruit and prawns, because 3th one rice has 350 colories

        /*
        Using dropWhile
        How about getting the other elements though? How about finding the elements that have
        greater than 320 calories? You can use the dropWhile operation for this:
         */
        List<Dish> slicedMenu2 = specialMenu.stream()
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(Collectors.toList());
        slicedMenu2.forEach(dish -> System.out.println(dish.getName()));

        /*
        The dropWhile operation is the complement of takeWhile. It throws away the elements at the
        start where the predicate is false. Once the predicate evaluates to true it stops and
        returns all the remaining elements, and it even works if there are an infinite number of
        remaining elements!
         */

        /*
        Truncating a stream
        Streams support the limit(n) method, which returns another stream that’s no longer than a
        given size. The requested size is passed as argument to limit. If the stream is ordered,
        the first elements are returned up to a maximum of n. For example, you can create a List by
        selecting the first three dishes that have more than 300 calories as follows:
         */
        List<Dish> dishes = specialMenu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .limit(3)
                .collect(Collectors.toList());
        /*
        only the first three elements that match the predicate are selected, and the result is
        immediately returned.

        Note that limit also works on unordered streams (for example, if the source is a Set). In
        this case you shouldn't assume any order on the result produced by limit.
         */

        /*
        Skipping elements
        Streams support the `skip(n)` method to return a stream that discards the first n
        elements. If the stream has fewer than n elements, an empty stream returned. Note that
        limit(n) and skip(n) are complementary! For example, the following code skips the first
        two dishes that have more than 300 calories and returns the rest.
         */
        List<Dish> skippedDishes = specialMenu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(Collectors.toList());

        List<Dish> first2MeatDishes = specialMenu.stream()
                .filter(d -> d.getType().equals(Dish.Type.MEAT))
                .limit(2)
                .collect(Collectors.toList());
        System.out.println("first2MeatDishes");
        first2MeatDishes.forEach(d -> System.out.println(d.getName()));

        /*
        Mapping

        A common data processing idiom is to select information from certain objects. For example,
        in SQL you can select a particular column from a table. The Streams API provides similar
        facilities through the `map` and `flatMap` methods.
         */

        List<String> dishNames = specialMenu.stream()
                .map(Dish::getName)
                .collect(Collectors.toList());

        /*
        Flattening streams

        You saw how to return the length for each word in a list using the map method. let's
        extend this idea a bit further: How could you return a list of all the unique characters
        for a list of words? For example, given the list of words ["hello", "world"] you'd like to
        return the list ["H," "e," "l," "o," "W," "r," "d"].
         */
        List<String> words = Arrays.asList("Hello", "World");
        words.stream()
                .map(word -> word.split(""))
                .distinct()
                .collect(Collectors.toList());
        /*
        The problem with this approach is that the lambda passed to the map method returns a
        String[] (an array of String) for each word. The stream returned by the map method is
        type of Stream<String[]>. What you want is Stream<String> to represent a stream of
        characters.

        Luckily there's a solution to this problem using the method `flatMap`! Let's see
        step-by-step how to solve it.

        * Attempt Using Map And Arrays.Stream
        First, you need a stream of characters instead of a stream of arrays. There's a method
        called Arrays.stream() that takes an array and produces a stream:
         */
        String[] arrayOfWords = {"Goodbye", "World"};
        Stream<String> streamOfWords = Arrays.stream(arrayOfWords);

        // Let's use Arrays.stream into previous problem

        words.stream()
                .map(word -> word.split(""))
                .map(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        // New attempt still doesn't solve our problem! This is because you now end up with a list
        // of streams (List<Stream<String>>).

        // Using can fix this problem by using `flatMap` as follows:

        List<String> uniqueLetters = words.stream()
                .map(word -> word.split(""))
                .flatMap(Arrays::stream) // flattens each generated stream into a single stream
                .distinct()
                .collect(Collectors.toList());

        System.out.println(uniqueLetters);

        /*
        Finding and matching

        Another common data processing idiom is finding whether some elements in as set of data
        match a given property. The Streams API provides such facilities through the allMatch,
        anyMatch, noneMatch, findFirst and findAny methods of a stream.

        Checking to see if a predicate matches at least one element
        The anyMatch method can be used to answer the question "Is there an element in the stream
        matching the given predicate?" For example, you can use it to find out whether the menu
        has a vegetarian option:
        */

        if (specialMenu.stream().anyMatch(Dish::isVegetarian)) {
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }

        // The anyMatch method returns a boolean and is therefore a terminal operation.

        /*
        Checking to see if predicate matches all elements
        The allMatch method works similarly to anyMatch but will check to see if all the elements
        of the stream match the given predicate. For example, you can use it to find out whether
        the  menu is healthy (all dishes are below 1000 calories):
         */
        boolean isHealthy = specialMenu.stream().allMatch(dish -> dish.getCalories() < 1000);

        System.out.println(isHealthy ? "All menu is healthy!" : "Menu is not fully healthy tho..");

        /*
        NoneMatch
        The opposite of allMatch is noneMatch. It ensures that no elements in the stream match the
        given predicate. For example, you could rewrite the previous example as follows using
        noneMatch:
         */
        boolean isHealthy2 = specialMenu.stream()
                .noneMatch(dish -> dish.getCalories() > 1000);

        System.out.println(isHealthy2 ? "All menu is healthy!" : "Menu is not fully healthy tho..");

        /*
        These three operations-anyMatch, allMatch, and noneMatch-make use of what we call
        short-circuiting, a stream version of the familiar Java short-circuiting && and ||
        operators.

        Short-circuiting evaluation
        Some operations don’t need to process the whole stream to produce a result. For
        example, say you need to evaluate a large boolean expression chained with and
        operators. You need only find out that one expression is false to deduce that the
        whole expression will return false, no matter how long the expression is; there’s no
        need to evaluate the entire expression. This is what short-circuiting refers to.
         */

        /*
        Finding an element

        The findAny method returns an arbitrary element of the current stream. It can be used in
        conjunction with other stream operations. For example, you may wish to find a dish that's
        vegetarian. You can combine the filter method and findAny to express this query:
         */
        List<Dish> nonVegetarianMenu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("pizza", false, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );
        Optional<Dish> dish = nonVegetarianMenu.stream()
                .filter(Dish::isVegetarian)
                .findAny();
        /*
        The stream pipeline will be optimized behind the scenes to preform a single pass and finish
        as soon as a result is found by using short-circuiting. But wait a minute; what’s
        this Optional thing in the code?

        Optional in a Nutshell

        The Optional<T> class (java.util.Optional) is a container class to represent the existence
        or absence of a value. In the previous code, it's possible that findAny doesn't find any
        element. Instead of returning null, which is well known for being error-prone, the Java 8
        library designers introduced Optional<T>. Some of the methods available in Optional:
            *isPresent: returns true if Optional contains a value, false otherwise
            *ifPresent(Consumer<T> block): executes the given block if a value is present.
            *T get(): returns the value if present; otherwise it throws a NoSuchElementException.
            *T orElse(T other) returns the value if present; otherwise it returns a default value.

        for example, in the previous example lets print the dish name if present
         */
        specialMenu.stream()
                .filter(Dish::isVegetarian)
                .findAny()
                .ifPresent(dish1 -> System.out.println(dish1.getName()));



    }
}
