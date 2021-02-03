package com.besimgurbuz.refactoringTestingAndDebugging;

public class RefactoringDesignPatternsWithLambdas {
    /*
    In this section, we explore five design patterns:

    - Strategy
    - Template method
    - Observer
    - Chain of responsibility
    - Factory

    How lambda expression can provide an alternative way to solve the problem that each design patter is intended to
    solve.
     */

    // Strategy Pattern
    public static class StrategyPattern {
        /*
        The strategy pattern is a common solution for representing a family of algorithms and letting you choose
        among them at runtime. You can apply this pattern to a multitude of scenarios, such as validating an input
        with different criteria, using different ways of parsing, or formatting an input.

        The strategy pattern consists of three parts;
            Client      ------>     Strategy ( execute() )    <------ ConcreteStrategyB
                                         ^-------------- ConcreteStrategyA

        - An interface to represent some algorithm ( the interface Strategy)
        - One or more concrete implementations of that interface to represent multiple algorithms (the concrete
        classes ConcreteStrategyA, ConcreteStrategyB)
        - One or more clients that use the strategy objects

        Suppose that you’d like to validate whether a text input is properly formatted for different criteria
        (consists of only lowercase letters or is numeric, for example). You start by defining an interface
        to validate the text (represented as a String ):
         */
        public interface ValidationStrategy {
            boolean execute(String s);
        }

        // Second, you define one or more implementation(s) of that interface:
        public static class IsAllLoverCase implements ValidationStrategy {

            @Override
            public boolean execute(String s) {
                return s.matches("[a-z]+");
            }
        }

        public static class IsNumeric implements ValidationStrategy {
            @Override
            public boolean execute(String s) {
                return s.matches("\\d+");
            }
        }

        // Then you can use these different validation strategies in your program:
        public static class Validator {
            private final ValidationStrategy strategy;
            public Validator(ValidationStrategy v) {
                this.strategy = v;
            }

            public boolean validate(String s) {
                return strategy.execute(s);
            }
        }

        public static void main(String[] args) {
            String case1 = "aaaa";
            String case2 = "bbb";

            Validator numericValidator = new Validator(new IsNumeric());
            boolean b1 = numericValidator.validate(case1);
            Validator lowerCaseValidator = new Validator(new IsAllLoverCase());
            boolean b2 = lowerCaseValidator.validate(case2);

            System.out.println(case1 + " is numeric -> " + b1);
            System.out.println(case2 + " is all lower case -> " + b2);

            // Refactoring with Lambdas:
            Validator numericValidator2 =
                    new Validator((String s) -> s.matches("[a-z]+"));
            boolean b3 = numericValidator.validate(case1);

            Validator allLowerCase =
                    new Validator((String s) -> s.matches("\\d+"));
            boolean b4 = allLowerCase.validate(case2);

            System.out.println("------With lambdas----");
            System.out.println(case1 + " is numeric -> " + b1);
            System.out.println(case2 + " is all lower case -> " + b2);
        }
    }
}
