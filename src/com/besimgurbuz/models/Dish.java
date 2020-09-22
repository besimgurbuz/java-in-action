
package com.besimgurbuz.models;
/**
 * @author Besim Gurbuz
 */
public class Dish {
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "name='" + name + '\'' +
                '}';
    }

    public int getCalories() {
        return calories;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        MEAT, OTHER, FISH
    }
}
