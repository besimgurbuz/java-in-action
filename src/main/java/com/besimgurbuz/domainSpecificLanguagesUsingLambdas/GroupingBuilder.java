package com.besimgurbuz.domainSpecificLanguagesUsingLambdas;

/*
    Collectors as a DSL to aggregate data

    You saw that the Stream interface can be viewed as a DSL that manipulates lists of data. Similarly, the
    Collector interface can be viewed as a DSL that performs aggregation on data.

    How Collector interface's methods are designed from a DSL point of view. In particular, as the methods in the
    Comparator interface can be combined to support multi-field sorting, Collector's can be combined to achieve
    multilevel grouping. You can group a list of cars, for example, first by their brand and then by their color
    as follows:
        (NESTING)
        Map<String, Map<Color, List<Car>>  carsByBrandAndColor =
                cars.stream().collect(groupingBy(Car::getBrand,
                                                 groupingBy(Car::getColor)));

    What do you notice here compared with what you did to concatenate two Comparators? You defined the multi-field
    Comparator by composing two Comparators in a fluent way,

        (FLUENT)
        Comparator<Person> comparator =
                comparing(Person::getAge).thenComparing(Person:::getName);
    Whereas the Collectors API allows you to create a multilevel Collector by nesting the Collectors:

        Collector<? super Car, ?, Map<Brand, Map<Color, List<Car>>>>
            carGroupingCollector =
                groupingBy(Car::getBrand, groupingBy(Car::getColor));

    Normally, the fluent style is considered to be more readable than the nesting style, especially when the
    composition involves three or more components. Is this difference in style a curiosity? In fact, it reflects
    a deliberate design choice caused by the fact that the innermost Collector has to be evaluated first, but
    logically, it's the last grouping to be performed. In this case, nesting the Collector creations with several
    static methods instead of fluently concatenating them allows the innermost grouping to be evaluated first but
    makes it appear to be the last one in the code.

    It would be easier (except for the use of generics in the definitions) to implement a GroupingBuilder that
    delegates to the groupingBy factory method but allows multiple grouping operations to be composed fluently.
    This next listing shows how.
 */

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;

public class GroupingBuilder<T, D, K> {
    private final Collector<? super T, ?, Map<K, D>> collector;

    public GroupingBuilder(Collector<? super T, ?, Map<K, D>> collector) {
        this.collector = collector;
    }

    public Collector<? super T, ?, Map<K, D>> get() {
        return collector;
    }

    public <J> GroupingBuilder<T, Map<K, D>, J> after(Function<? super T, ? extends J> classifier) {
        return new GroupingBuilder<>(groupingBy(classifier, collector));
    }

    public static <T, D, K> GroupingBuilder<T, List<T>, K> groupOn(Function<? super T, ? extends K> classifier) {
        return new GroupingBuilder<> (groupingBy(classifier));
    }
}

/*
    What is the problem with this fluent builder? Trying to use it makes the problem evident:

    Collector<? super Car, ?, Map<Brand, Map<Color, List<Car>>>>
        carGroupingCollector =
            groupOn(Car::getColor).after(Car::getBrand).get();

    As you can see, the use of this utility class is counterintuitive because the grouping functions have to be
    written in reverse order relative to the corresponding nested grouping level. If you try to refactor this
    fluent builder to fix the ordering issue, you realize that unfortunately, the Java type system won't allow you
    to do this.

    By looking more closely at the native Java API and the reason behind its design decisions, you've started to
    learn a few patterns and useful tricks for implementing readable DSLs. In the next section, you continue to
    investigate techniques for developing effective DSLs.
 */


