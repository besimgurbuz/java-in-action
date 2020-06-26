package com.besimgurbuz.methodreference;

import com.besimgurbuz.lambdas.LambdasMain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

/**
 * @author Besim Gurbuz
 */
public class MethodReferenceMain {

    static class Fruit {
        int weight;

        public Fruit(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return this.weight + "g Fruit";
        }
    }

    static class Apple extends Fruit {
        public Apple(int weight) {
            super(weight);
        }
    }

    static class Orange extends Fruit {
        public Orange(int weight) {
            super(weight);
        }

        @Override
        public String toString() {
            return super.toString() + " of Orange";
        }
    }

    // Great example of Method Reference
    static Map<String, Function<Integer, Fruit>> map = new HashMap<>();
    static {
        map.put("apple", Apple::new);
        map.put("orange", Orange::new);
    }

    public static Fruit giveMeFruit(String fruit, Integer weight) {
        return map.get(fruit.toLowerCase()).apply(weight);
    }

    // Constructor references Quiz

    /*
     *  You saw how to transform zero, one and two argument constructors into constructor
     * references. What would you need to do in order to use a constructor reference for a
     * three-argument constructor such as RGB (int, int, int)
     */
    static class RGB {
        int red, green, blue;

        public RGB(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
    // We need to create a functional interface with given signature
    @FunctionalInterface
    static interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
    // now we can use it
    static {
        TriFunction<Integer, Integer, Integer, RGB> colorFactory = RGB::new;
    }

    public static void main(String[] args) {
        /*
         *  Method References
         *  1   Lambda =>
         *          (args) -> ClassName.staticMethod(args)
         *      Method Reference
         *          ClassName::staticMethod
         *
         *  2   Lambda =>
         *          (arg0, rest) -> arg0.instanceMethod(rest)
         *      Method Reference
         *          ClassName::instanceMethod       * arg0 is of type ClassName
         *
         *  3   Lambda =>
         *          (args) -> expr.instanceMethod(args)
         *      Method Reference
         *          expr::instanceMethod
         *
         */

        List<String> strings = Arrays.asList("a", "b", "A", "B");
        strings.sort(String::compareToIgnoreCase);

        // Method References Quiz
        ToIntFunction<String> stringToInt = (String s) -> Integer.parseInt(s);
        // With method reference
        ToIntFunction<String> stringToInt2 = Integer::parseInt;

        BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);
        // With method reference
        BiPredicate<List<String>, String> contains2 = List::contains;

        Predicate<String> startsWithNumber = (String string) -> startsWithNumber(string);
        // With method reference
        Predicate<String> startsWithNumber2 = MethodReferenceMain::startsWithNumber;
        System.out.println(startsWithNumber.test("1asd"));

        // Constructor references
        Supplier<LambdasMain.Student> createStudent = () -> new LambdasMain.Student();
        // with method reference
        Supplier<LambdasMain.Student> createStudent2 = LambdasMain.Student::new;
        // Constructors with arguments
        BiFunction<Integer, String, LambdasMain.Student> createStudentWithValues = (id, name) ->
                new LambdasMain.Student(id, name);
        // with method reference
        BiFunction<Integer, String, LambdasMain.Student> createStudentWithValues2 = LambdasMain.Student::new;

        System.out.println(giveMeFruit("ORANGE", 3));

    }

    public static boolean startsWithNumber(String string) {
        try {
            Integer.parseInt(string.substring(0, 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
