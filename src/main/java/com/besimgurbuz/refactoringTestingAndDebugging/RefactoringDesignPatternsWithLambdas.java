package com.besimgurbuz.refactoringTestingAndDebugging;

import com.besimgurbuz.refactoringTestingAndDebugging.mockData.Customer;
import com.besimgurbuz.refactoringTestingAndDebugging.mockData.MockData;

import java.util.function.Consumer;

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

    // Template Method
    public static class TemplateMethodPattern {
        /*
        The template method design pattern is a common solution when you need to represent the outline of an
        algorithm and have the additional flexibility to change certain parts of it. Ok, this pattern sounds a bit
        abstract. In other words, the template method pattern is useful when you find yourself saying "I'd love
        to use this algorithm, but I need to change a few lines so it does what I want."

        Here's an example of how this pattern works. Suppose that you need to write a simple online banking app.
        Users typically enter a customer ID; the application fetches the customer's details from the bank's
        database and does something to make the customer happy. Different online banking applications for different
        banking branches may have different ways of making a customer happy (such as adding a bonus to his account
        or sending him less paperwork). You can write the following abstract class to represent the online banking
        application:
         */
        abstract static class OnlineBanking {
            public void processCustomer(int id) {
                Customer c = MockData.Customers.getCustomerById(id);
                makeCustomerHappy(c);
            }

            abstract void makeCustomerHappy(Customer c);
        }
        /*
        The processCustomer method provides a sketch for the online banking algorithm: Fetch the customer given its
        ID and make the customer happy. Now different branches can provide different implementations of the
        makeCustomerHappy method by subclassing the OnlineBanking class.
         */

        // Using Lambda Expressions
        /*
        You can tackle the same problem (creating an outline of an algorithm and letting implementers plug in some
        parts) by using you favorite lambdas. The components of the algorithms you want to plug in can be
        represented by lambda expressions or method references.

        Here, we introduce a second argument to the processCustomer method of type Consumer<Customer> because it
        matches the signature of the method makeCustomerHappy defined earlier:
         */
        public static class OnlineBankingLambda {
            public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
                Customer c = MockData.Customers.getCustomerById(id);
                makeCustomerHappy.accept(c);
            }
        }
        /* Now you can plug in different behaviors directly without subclassing the OnlineBanking class by passing
        lambda expressions:
         */

        public static void main(String[] args) {
            new OnlineBankingLambda().processCustomer(2, (Customer c) -> System.out.println("Hello " + c.getName()));
        }

        // This example shows how lambda expressions can help you remove the boilerplate inherent to design patterns
    }
}
