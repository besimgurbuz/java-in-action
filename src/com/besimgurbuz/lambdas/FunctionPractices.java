package com.besimgurbuz.lambdas;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author Besim Gurbuz
 */
public class FunctionPractices {
    /*
     * The java.utils.function.Function<T, R> interface defines an abstract method named apply that
     * takes an object of generic type T as input and returns an object of generic type R.
     * You might use this interface when you need to define a lambda that maps information from an
     * input object to an output (for example, extracting the weight of an apple or mapping a
     * string to its length).
     *
     * Example about Function interface with Predicate
     *  - Let's say you have a Person class which has 2 fields one is age and second is name
     *  - And you want to filter adult people after you want take just first names from there names.
     *  - Let's solve this scenario!
     */
    public static class Person {
        private Integer age;
        private String name;

        public Person(Integer age, String name) {
            this.age = age;
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static void main(String[] args) {
        String[] names = {"Foo", "Ada", "Ay", "Test"};
        List<Integer> stringSizes = Arrays.asList(names)
                .stream()
                .map(String::length)
                .collect(Collectors.toList());

        stringSizes.forEach(System.out::println);

        // SOLVING OF EXAMPLE STARTS
        List<Person> people = Arrays.asList(
                new Person(12, "Jakie Love"),
                new Person(35, "Artur Morgan"),
                new Person(20, "Ahmet Yilmaz"),
                new Person(10, "Sarah Wood")
        );

        Function<Person, String> destructName = (Person person) ->
                person.getName().split(" ")[0];

        // Function compose
        people.stream()
                .filter(person -> person.getAge() > 18)
                .map(destructName.compose(person -> {
                    person.setName(person.getName().toUpperCase());
                    return person;
                }))
                .forEach(System.out::println);

        // Function andThen

        Function<Person, Integer> getAge = Person::getAge;

        people.stream()
                .map(getAge.andThen((Integer age) -> LocalDate.now().getYear() - age))
                .forEach(birthYear -> System.out.println("Birth year is " + birthYear));

        // PRIMITIVE SPECIAL Function Interfaces

        IntFunction<String> takeIntReturnString = (int value) -> UUID
                .randomUUID().toString().substring(0, value);

        String uid = takeIntReturnString.apply(10);
        System.out.println(uid);

        IntToDoubleFunction halfOfValue = (int value) -> value / 2.0;

        System.out.println(halfOfValue.applyAsDouble(5));

        ToDoubleFunction<String> generateDouble = (String str) -> {
            return str.length() / 2.0;
        };

    }

}
