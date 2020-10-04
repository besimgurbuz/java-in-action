package com.besimgurbuz.lambdas;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Besim Gurbuz
 */
public class ConsumerPractices {
    /**
     * The java.util.function.Consumer<T> interface defines an abstract method named accept that takes an object of generic type T and returns no result (void). You might use this interface when you need to access an object of type T and perform some operations on it. For example, you can use it to create a method forEach, which takes a list of Integers and applies an operation on each element of that list. In the following listing, you'll use this forEach method combined with a lambda to print all the elements of the list.
     *
     */

    static class MyNumber {
        private Integer value;

        public MyNumber(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value + ".";
        }
    }

    public static void main(String[] args) {
        Consumer<MyNumber> multiplyByTwo = (MyNumber val) -> {
            val.setValue(val.getValue() * 2);
        };

        Consumer<MyNumber> addOne = (MyNumber val) -> val.setValue(val.getValue() + 1);

        List<MyNumber> numbers = Arrays.asList(new MyNumber(1), new MyNumber(5));

        numbers.forEach(multiplyByTwo.andThen(addOne).andThen(System.out::println));
    }
}
