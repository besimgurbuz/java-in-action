package com.besimgurbuz.lambdas;

import java.util.function.Predicate;

/**
 * @author Besim Gurbuz
 */
public class PredicatePractices {

    public static void main(String[] args) {

        Predicate<Integer> predicate1 = (val) -> val > 0;
        Predicate<Integer> predicate2 = (val) -> val < 20;
        Predicate<Object> predicate3 = Predicate.isEqual("MY_STRING");
        Predicate<Integer> notPredicate1 = Predicate.not(predicate1);

        boolean result = predicate1.or(predicate2).test(21);

        System.out.println(result);
        System.out.println(predicate3.test("MY_STRIN"));


    }
}
