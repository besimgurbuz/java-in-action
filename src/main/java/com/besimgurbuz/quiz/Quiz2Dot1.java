package com.besimgurbuz.quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Besim Gurbuz
 */
public class Quiz2Dot1 {
    /*
    Write a prettyPrintApple method that takes a List of Apples and that can be parameterized with multiple ways to generate a string output from an apple.
     */

    static enum Color {
        RED, GREEN
    }

    static class Apple {
        Integer weight;
        Color color;

        public Apple(Integer weight, Color color) {
            this.weight = weight;
            this.color = color;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public Integer getWeight() {
            return weight;
        }

        public Color getColor() {
            return color;
        }
    }

    public static void prettyPrintApple(List<Apple> inventory, AppleFormatter formatter) {
        for (Apple apple :
                inventory) {
            String output = formatter.accept(apple);
            System.out.println(output);
        }
    }

    // Answer

    public interface AppleFormatter {
        String accept(Apple a);
    }

    public static class AppleFancyFormatter implements AppleFormatter {
        @Override
        public String accept(Apple a) {
            String characteristic = a.getWeight() > 150 ? "heavy" : "light";
            return "A " + characteristic + " " + a.getColor() + " apple";
        }
    }

    public static class AppleSimpleFormatter implements AppleFormatter {
        @Override
        public String accept(Apple a) {
            return "An apple of " + a.getWeight() + "g";
        }
    }

    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        inventory.add(new Apple(12, Color.GREEN));
        inventory.add(new Apple(200, Color.RED));
        inventory.add(new Apple(120, Color.RED));
        inventory.add(new Apple(230, Color.GREEN));
        System.out.println("--FANCY--");
        prettyPrintApple(inventory, new AppleFancyFormatter());
        System.out.println("--SIMPLE--");
        prettyPrintApple(inventory, new AppleSimpleFormatter());
        System.out.println("--LAMBDA #1--");
        prettyPrintApple(inventory, (a) -> "An apple color of " + a.getColor());
        System.out.println("--LAMBDA #2--");
        prettyPrintApple(inventory, (Apple apple) -> apple.getWeight() + "g " + apple.getColor() + " apple.");
    }

    static class MeaningOfThis {
        public final int value = 4;

        public void doIt() {
            int value = 6;
            Runnable r = new Runnable() {
                public final int value = 5;
                @Override
                public void run() {
                    int value = 10;
                    System.out.println(this.value);
                }
            };
            r.run();
        }

        public static void main(String[] args) {
            MeaningOfThis m = new MeaningOfThis();
            m.doIt();
        }
    }
}
