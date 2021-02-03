package com.besimgurbuz.refactoringTestingAndDebugging;

import com.besimgurbuz.refactoringTestingAndDebugging.mockData.Customer;
import com.besimgurbuz.refactoringTestingAndDebugging.mockData.MockData;

import java.util.ArrayList;
import java.util.List;
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

    // Observer
    public static class ObserverPattern {
        /*
        The observer design pattern is a common solution when an object (called the subject) needs to
        automatically notify a list fo other objects (called observers) when some event happens (such as state
        change). You typically come across this pattern when working with GUI applications. You register a set of
        observers on a GUI component such as a button. If the button is clicked, the observers are notified and can
        execute a specific action. But the observer pattern isn't limited to GUIs. The observer design pattern is
        also suitable in a situation in which several traders (observers) want to react to the change of price of a
        stock (subject).

            Subject + notifyObserver()      <>------->      Observer + notify()     <----------   ConcreteObserverB
                                                                    ^-------------       ConcreteObserverA
        Let's write an example; The concept is simple: several newspaper agencies (The New York Times, The
        Guardian, and Le Monde) are subscribed to a feed of news tweets and may want to receive a notification
        if a tweet contains a particular keyword.

        First, you need an Observer interface that groups the observers. It has one method, called notify, that will
        be called by the subject (Feed) when a new tweet is available:
         */

        interface Observer {
            void notify(String tweet);
        }
        /*
        Now you can declare different observers (here, the three newspapers) that produce a different action for
        each different keyword contained in a tweet:
         */

        static class NYTimes implements Observer {
            public void notify(String tweet) {
                if(tweet != null && tweet.contains("money")){
                    System.out.println("Breaking news in NY! " + tweet);
                }
            }
        }
        static class Guardian implements Observer {
            public void notify(String tweet) {
                if(tweet != null && tweet.contains("queen")){
                    System.out.println("Yet more news from London... " + tweet);
                }
            }
        }
        static class LeMonde implements Observer {
            public void notify(String tweet) {
                if(tweet != null && tweet.contains("wine")){
                    System.out.println("Today cheese, wine and news! " + tweet);
                }
            }
        }

        // You're still missing the crucial part: the subject. Define an interface for the subject:
        interface Subject {
            void registerObserver(Observer o);
            void notifyObservers(String tweet);
        }

        /*
        The subject can register a new observer using the registerObserver method and notify his observers of a
        tweet with the notifyObservers method. Now implement the Feed class:
         */

        static class Feed implements Subject {
            private final List<Observer> observers = new ArrayList<>();
            @Override
            public void registerObserver(Observer o) {
                observers.add(o);
            }

            @Override
            public void notifyObservers(String tweet) {
                observers.forEach(observer -> observer.notify(tweet));
            }

            public void clearObservers() {
                observers.clear();
            }
        }

        public static void main(String[] args) {
            Feed f = new Feed();
            f.registerObserver(new NYTimes());
            f.registerObserver(new Guardian());
            f.registerObserver(new LeMonde());
            f.notifyObservers("The queen said her favourite book is Modern Java in Action!");

            // Using lambda expressions:
            f.clearObservers();
            f.registerObserver((String tweet) -> {
                if (tweet != null && tweet.contains("money")) {
                    System.out.println("Breaking news in NY! " + tweet);
                }
            });

            f.registerObserver((String tweet) -> {
                if (tweet != null && tweet.contains("queen")) {
                    System.out.println("Yet more news from London... " + tweet);
                }
            });

            f.notifyObservers("Bitcoin is earning investors a lot of money!");
        }

        /*
        Should you use lambda expressions all the time? The answer is no. In the example we described, lambda
        expressions work great because the behavior to execute is simple, so they’re helpful for removing
        boilerplate code. But the observers may be more complex; they could have state, define several methods,
        and the like. In those situations, you should stick with classes.
         */
    }
}
