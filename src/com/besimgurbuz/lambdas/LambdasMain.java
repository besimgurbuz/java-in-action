package com.besimgurbuz.lambdas;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

/**
 * @author Besim Gurbuz
 */
public class LambdasMain {
    /*
        A lambda expression can be understood as a concise representation of an anonymous function
        that can be passed around. It doesn't have a name, but it has a list of parameters, a body,
        a return type and also possibly a list of exceptions that can be thrown. That's one big
        definition; let's break it down:
            - Anonymous -> We say anonymous because it doesn't have an explicit name like a method
            would normally have; less to write and think about!
            - Function -> We say function because a lambda isn't associated with a particular class
            like a method is. But like a method, a lambda has a list of parameters, a body, a
            return type, and a possible list of exceptions that can be thrown.
            - Passed around -> A lambda expression can be passed as argument to a method or stored
            in a variable.
            - Concise -> you don't need to write a lot of boilerplate like you do for anonymous
            classes.
     */

    static Runnable r1 = () -> System.out.println("Hello World!");
    static Runnable r2 = new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello World 2");
        }
    };

    public static void process(Runnable r) {
        r.run();
    }

    public static class Student {
        private int id;
        private String name;

        public Student() {}

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

//    public static void main(String[] args) {
//        process(r1);
//        process(r2);
//        process(() -> System.out.println("Hello World 3"));
//    }

    // EXAMPLE WITH FUNCTIONAL INTERFACE
    // after creating BufferedReaderProcessor functional interface we can use lambda expression

    public static String processFile(BufferedReaderProcessor processor) throws IOException {
        String filePath = "src/com/besimgurbuz/lambdas/data.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return processor.process(br); // process the bufferedReader object
        }
    }

    public static void main(String[] args) throws IOException {
        String oneLine = processFile((BufferedReader br) -> br.readLine());

        String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());

        System.out.println(oneLine);
        System.out.println("-------");
        System.out.println(twoLines);

        /*
         *  Java type is either a reference type (for example, Byte, Integer, Object, List) or a
         * primitive type (for example, int, double, byte, char). But generic parameters (for
         * example, the T in Consumer<T>) can be bound only to reference types. This is due to how
         * generics are internally implemented. As a result in Java there's a mechanism to convert
         * a primitive type into a corresponding reference type. This mechanism is called boxing.
         * The opposite approach (converting a reference type into a corresponding primitive type)
         * is called unboxing. Java also has an autoboxing mechanism to facilitate the task for
         * programmers: boxing and unboxing operations are node automatically.
         *
         *  But this comes with performance cost. Boxed values are a wrapper around primitive types
         * and are stored on the heap. Therefore, boxed values use more memory and require
         * additional memory lookups to fetch the wrapped primitive value.
         *
         *  Java 8 also added a specialized version of the functional interfaces we used earlier in
         * order to avoid autoboxing operations when the inputs or outputs are primitives. For
         * example,
         */
        IntPredicate evenNumbers = (int i) -> i % 2 == 0;
        evenNumbers.test(1000); // Boxing not happened
        Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
        oddNumbers.test(1000); // Boxing happened

        // T -> R > Function<T, R>
        // (int, int) -> int > IntBinaryOperator
        // T -> void > Consumer<T>
        // () -> T > Supplier<T>
        // (T, U) -> R > BiFunction<T, U, R>

        Predicate<List<String>> stringPredicate = (List<String> list) -> list.isEmpty();
        Supplier<String> stringSupplier = () -> "My supplier";
        Consumer<String> stringConsumer = (String str) -> System.out.println(str.substring(0, 4));
        Function<String, Integer> stringIntegerFunction = (String s) -> s.length(); // Could be ToIntFunction<String>
        IntBinaryOperator intBinaryOperator = (int a, int b) -> a * b;
        Comparator<String> stringComparator = (String s1, String s2) -> s1.substring(0, 2).compareTo(s2.substring(0, 2)); // Could be BiFunction<String, String, Integer>, ToIntBiFunction<String, String>

        // If you need the body of a lambda expression to throw an exception: define your own
        // func.interface that declares the checked exception, or wrap the lambda body with
        // a try/catch block

        Function<BufferedReader, String> f =
                (BufferedReader b) -> {
                    try {
                        return b.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
        // Using Local Variables
        int portNumber = 3000;
        Runnable runnable = () -> System.out.println(portNumber);
        //portNumber = 4500;
        runnable.run();
        /*
         *  Restrictions on Local Variables
         * You may be asking yourself why local variables have these restrictions. First, there's
         * a key difference in how instance and local variables are implemented behind the scenes.
         * Instance variables are stored on the heap, whereas local variables live on the stack. If
         * a lambda could access the local variable directly and the lambda was used in a thread,
         * then the thread using the lambda could try to access the variable after the thread that
         * allocated the variable had deallocated it. Hence, Java implements access to a free local
         * variable as access to a copy of it, rather than access to the original variable. This
         * makes no difference if the local variable is assigned to only once - hence the
         * restriction.
         */
    }
}