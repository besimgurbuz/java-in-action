package com.besimgurbuz;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Besim Gurbuz
 */
public class GeneralPractices {
    static enum Color {
        RED, GREEN
    }

    static class Apple {
        int weight;
        String country;
        Color color;

        public Apple(int weight, String country, Color color) {
            this.weight = weight;
            this.country = country;
            this.color = color;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return this.color.toString().toLowerCase()
                    + " apple from " + this.country + " " + this.weight + "g.";
        }
    }

    public static void main(String[] args) {
        // Comparator
        List<Apple> inventory = Arrays.asList(
                new Apple(10, "Turkey", Color.RED),
                new Apple(200, "England", Color.GREEN),
                new Apple(50, "USA", Color.RED),
                new Apple(300, "Canada", Color.GREEN),
                new Apple(300, "France", Color.GREEN)
        );
        System.out.println("Addition order -> ");
        inventory.forEach(System.out::println);
        System.out.println("After sorting -> ");
        // sort by weight
        inventory.sort(Comparator.comparing(Apple::getWeight)
                .reversed()
                .thenComparing(Apple::getCountry));
        inventory.forEach(System.out::println);

        // Predicates
        Predicate<Apple> redApple = (Apple apple) -> apple.getColor().equals(Color.RED);
        Predicate<Apple> notRedApple = redApple.negate();

        System.out.println(redApple.test(inventory.get(3)));
        System.out.println(notRedApple.test(inventory.get(3)));

        Predicate<Apple> heavyApple =
                (Apple apple) -> apple.getWeight() > 150;

        Predicate<Apple> heavyAndCanadian = heavyApple
                .and(apple -> apple.getCountry().equals("Canada"));

        System.out.println(heavyAndCanadian.test(inventory.get(0)));

        // Functions
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        Function<Integer, Integer> h = f.andThen(g);
        int result = h.apply(1);
        System.out.println(result);
    }
}
