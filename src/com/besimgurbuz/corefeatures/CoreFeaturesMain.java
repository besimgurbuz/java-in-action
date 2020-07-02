package com.besimgurbuz.corefeatures;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

/**
 * @author Besim Gurbuz
 */
public class CoreFeaturesMain {

    static enum Color {
        GREEN, RED
    }

    static class Apple {
        Integer weight;
        Color color;

        Apple() {}

        Apple(int weight) {
            this.weight = weight;
        }

        Apple(int weight, Color color) {
            this.weight = weight;
            this.color = color;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return "Weight: " + this.weight + "kg "+ this.color +" apple.";
        }
    }

    public static void runFunc(Object arg, Function function) {
        function.apply(arg);
    }

    public static void main(String[] args) {

        runFunc("Besim", (name) -> {
            System.out.println("Hello there! " + name);
            return "Hello there! " + name;
        });
        

        List<Apple> inventory = new ArrayList<>();
        inventory.add(new Apple(1));
        inventory.add(new Apple(3));
        inventory.add(new Apple(2));

        // Old Way
        Collections.sort(inventory, new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });
        for (Apple a :
                inventory) {
            System.out.println(a);
        }
        System.out.println("--------------------------");

        inventory.add(new Apple(12));
        inventory.add(new Apple(5));
        // Java 8
        inventory.sort(comparing(Apple::getWeight));
        for (Apple a :
                inventory) {
            System.out.println(a);
        }

        // Old Way
        File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isHidden();
            }
        });

        // Java 8
        File[] hiddenFiles2 = new File(".").listFiles(File::isHidden);

        System.out.println("Hidden files->");
        for (File file :
                hiddenFiles) {
            System.out.println(file);
        }
        System.out.println("----------------------");
        for (File file :
                hiddenFiles2) {
            System.out.println(file);
        }

        testFiltering();
    }

    // DANGEROUS MOVES!
    /*
        Let say, you want to filter inventory of apples by apple's color
        with old way
     */
    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple :
                inventory) {
            if (Color.GREEN.equals(apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }
    // AFTER YOUR CODE SOMEONE WANT TO FILTER BY WEIGHT AND SEE YOUR CODE AND Copy Pasted it...
    public static List<Apple> filterHeavyApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple :
                inventory) {
            if (apple.getWeight() > 150) {
                result.add(apple);
            }
        }
        return result;
    }

    // BUT WITH JAVA 8 WE ARE ABLE TO DO LIKE THIS
    public static boolean isGreenApple(Apple apple) {
        return Color.GREEN.equals(apple.getColor());
    }

    public static boolean isHeavyApple(Apple apple) {
        return apple.getWeight() > 150;
    }

    static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }

    // USAGE OF THESE FILTER METHODS ARE
    public static void testFiltering() {
        List<Apple> inventory = new ArrayList<>();
        inventory.add(new Apple(150, Color.RED));
        inventory.add(new Apple(200, Color.GREEN));
        inventory.add(new Apple(180, Color.RED));
        inventory.add(new Apple(2, Color.RED));
        inventory.add(new Apple(10, Color.RED));

        for (Apple filteredApple : filterApples(inventory, CoreFeaturesMain::isGreenApple)) {
            System.out.println(filteredApple);
        }

        List<Apple> filtered = filterApples(inventory, (apple) -> apple.getWeight() < 50);
        for (Apple apple : filtered) {
            System.out.println(apple);
        }

        // Streams API
        /*
            Using streams:
            1.Convert iterable to stream
            2.Process it in parallel
            3.Convert back to list
         */
        List<Apple> heavyApples = inventory.stream()
                .filter((Apple a) -> a.getWeight() > 150)
                .collect(Collectors.toList());

        // Parallel processing
        List<Apple> redApples = inventory.parallelStream()
                .filter((Apple a) -> a.getColor().equals(Color.RED))
                .collect(Collectors.toList());
        System.out.println("/PARALLEL");
        redApples.forEach(System.out::println);

        Runnable runnable = () -> {
            System.out.println("Runnable is running!");
        };

        runnable.run();

        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });

        inventory.sort((a1, a2) -> a1.getWeight().compareTo(a2.getWeight()));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("This runnable running at a newly created thread!");
            }
        });

        Thread t2 = new Thread(() -> System.out.println("This is another newly created thread!"));

        t.start();
        t2.start();

        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<String> threadName = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return Thread.currentThread().getName();
            }
        });

        Future<String> threadNameV8 = executorService.submit(() -> Thread.currentThread().getName());

        try {
            System.out.println(threadName.get());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
