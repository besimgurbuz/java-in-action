package com.besimgurbuz.refactoringTestingAndDebugging;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImprovingCodeReadabilityAndFlexibility {
    ImprovingCodeReadabilityAndFlexibility() throws IOException {
    }
   /*
        What does it mean improve the readability of code? Defining good readability can be subjective. The general
    view is that the term means "how easily this code can be understood by another human." Improving code
    readability ensures that your code is understandable and maintainable by people other than you. You can take a
    few steps to make sure that your code is understandable and follows coding standards.

        Using features introduces in Java 8 can also improve code readability compared with previous versions. You can
    reduce the verbosity of your code, making it easier to understand. Also, you can better show the intent of your
    code by using method references and the Streams API.
        In this chapter, we describe three simple refactorings that use lambdas, method references, and streams,
    which you can apply to your code to improve its readability:

        - Refactoring anonymous classes to lambda expressions
        - Refactoring lambda expressions to method references
        - Refactoring imperative-style data processing to streams
    */

    // From anonymous classes to lambda expressions
   /*
   The first simple refactoring you should consider is converting uses of anonymous classes implementing one single
   abstract method to lambda expressions. Why? We hope that in earlier chapters, we convinced you that anonymous
   classes are verbose and error-prone. By adopting lambda expressions, you produce code that's more succinct and
   As shown in chapter 3, here's an anonymous class for creating a Runnable object and its lambda-expression
   counterpart:
    */
    static Runnable r1 = new Runnable() { // Before, using an anonymous class
        @Override
        public void run() {
            System.out.println("Hello");
        }
    };

    static Runnable r2 = () -> System.out.println("Hello from lambda"); // After, using a lambda expression

   /*
   But converting anonymous classes to lambda expressions can be a difficult process in certain situations. First,
   the meaning of this and super are different for anonymous classes and lambda expressions. Inside an anonymous
   this refers to the anonymous class itself, but inside a lambda, it refers to the enclosing class. Second,
   anonymous classes are allowed to shadow variables from the enclosing class. Lambda expressions can't (they'll
   cause a compile error), as shown the following code:

   int a = 10;
   Runnable r1 = () -> {
        int a = 2;                  // COMPILE ERROR
        System.out.println(a);
   };
   Runnable r2 = new Runnable() {
        public void run() {
            int a = 2;             // EVERYTHING IS FINE
            System.out.println(a);
       }
   };

   Finally, converting an anonymous class to a lambda expression can make the resulting code ambiguous in the
   context of overloading. Indeed, the type of anonymous class is explicit at instantiation, but the type of
   the lambda depends on its context. Here's an example of how this situation can be problematic. Suppose that
   you've declared a functional interface with the same signature as Runnable, here called Task (as might occur
   when you need more-meaningful interface names in your domain model):

   interface Task {
        public void execute();
   }
   public static void doSomething (Runnable r) { r.run(); }
   public static void doSomething (Task a) { a.execute(); }

   Now you can pass an anonymous class implementing Task without a problem:
   doSomething(new Task() {
        public void execute() {
            System.out.println("Danger danger!");
        }
   });

   But converting this anonymous class to a lambda expression results in an ambiguous method call, because both
   Runnable and Task are valid target types:

   doSomething(() -> System.out.println("Danger danger!!"));
                                       **problem; both doSomething(Runnable) and doSomething(Task) match.

   You can solve the ambiguity by providing an explicit cast (Task):
   doSomething((Task) () -> System.out.println("Danger danger!"));
    */

    // From lambda expressions to method references
   /*
   Lambda expression are great for short code that need to be passed around. But consider using method references
   whenever possible to improve code readability. A method name states the intent of your code more clearly.
   In chapter 6, for example, we showed you the following code to group dishes by caloric levels:

   Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
        menu.stream()
            .collect(
                groupingBy(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                }));
   You can extract the lambda expression into a separate method and pass it as an argument to groupingBy. The
   code becomes more concise, and its intent is more explicit:

   Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
        menu.stream().collect(groupingBy(Dish::getCaloricLevel());

   class Dish {
        ...
        ...
        public CaloricLevel getCaloricLevel() {
            if (this.getCalories() <= 400) return CaloricLevel.DIET;
            else if (this.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }
   }

   In addition, consider using helper static methods such as comparing and maxBy whenever possible. These
   methods were designed for use with method references! Indeed, this code states much more clearly its intent
   than its counterpart using a lambda expression, as we showed you in chapter 3:

   inventory.sort(
        (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
   inventory.sort(comparing(Apple::getWeight));

   Moreover, for many common reduction operations such as sum, maximum, there are built-in helper methods that
   can be combined with method references. We showed you, for example, that by using the Collectors API, you can
   find the maximum or sum in a clearer way than by using a combination of a lambda expression and a lower-level
   reduce operation. Instead of writing

   int totalCalories =
        menu.steam().map(Dish::getCalories)
                    .reduce(0, (c1,c2) -> c1 + c2);

   try using alternative built-in collectors, which state the problem statement more clearly. Here, we use the
   collector summingInt (names go a long way in documenting your code):

   int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
    */

    // From imperative data processing to Streams
   /*
   Ideally, you should try to convert all code that processes a collection with typical data processing patterns
   with an iterator to use the Streams API instead. Why? The Streams API expresses more clearly the intent of a
   data processing pipeline. In addition, streams can be optimized behind the scenes, making use of short-circuiting
   and laziness as well as leveraging your multicore architecture, as we explained in chapter 7.

   The following imperative code expresses two patterns (filtering and extracting) that are mangled together,
   forcing the programmer to carefully figure out the whole implementation before figuring out what the code does.
   In addition, an implementation that executes in parallel would be a lot more difficult to write.

   List<String> dishNames = new ArrayList<>();
   for (Dish dish : menu) {
        if (dish.getCalories() > 300) {
            dishName.add(dish.getName());
        }
   }

   The alternative, which uses the Streams API, reads more like the problem statement, and it can be easily
   parallelized:

   menu.parallelStream()
        .filter(d -> d.getCalories() > 300)
        .map(Dish::getName)
        .collect(toList());

   Unfortunately, converting imperative code to the Streams API can be a difficult task, because you need to
   think about control-flow statements such as break, continue, and return and then infer the right stream
   operations to use. The good news is that some tools can hep you with this task as will.
   take a look https://ieeexplore.ieee.org/document/6606699
    */

    // Improving code flexibility
   /*
   We argued in chapter 2 and 3 that lambda expressions encourage the style of behavior parameterization. You can
   represent multiple behaviors with different lambdas that you can then pass around to execute. This style lets
   you cope with requirement changes (creating multiple ways of filtering with a Predicate or comparing with a
   Comparator, for example). In the next section, we look at a couple fo patterns that you can apply to your code
   base to benefit immediately from lambda expressions.

   Adopting Functional Interfaces
   First, you can't use lambda expressions without functional interfaces; therefore, you should start introducing
   them in your code base. But in which situations should you introduce them? In this chapter, we discuss two
   common code patterns that can be refactored to leverage lambda expressions: conditional deferred execution and
   execute around. Also, in the next section, we show you how various object-oriented design patterns-such as the
   strategy and template-method design patterns-can be rewritten more concisely with lambda expressions.

   Conditional Deferred Execution
   It's common to see control-flow statement mangled inside business-logic code. Typical scenarios include
   security checks and logging. Consider the following code, which uses the build-in Java Logger class:

   if (logger.isLoggable(Log.FINER)) {
        logger.finer("Problem: " + generateDiagnostic());
   }

   What's wrong with it? A couple of things:
   - The state of the logger (what level it supports) is exposed in the client code through the method isLoggable.
   - Why should you have to query the state of the logger object every time before you log a message? It clutters
   your code.

   A better alternative is to use the log method, which checks internally to see whether the logger object is set to
   the right level before logging the message

   logger.log(Level.FINER, "Problem: " + generateDiagnostic());

   This approach is better because your code isn't cluttered with if checks, and the state of the logger is no
   longer exposed. Unfortunately, this code still has an issue: the logging message is always evaluated, even if
   the logger isn't enabled for the message level passed as an argument.

   Lambda expressions can help. What you need i a way to defer the construction of the message so that it can be
   generated only under a given condition (here, when logger level is set to FINER). It runs out that the Java 8
   API designers knew about this problem and introduced an overloaded alternative to log that takes a Supplier as an
   argument. This alternative log method has the following signature:

   public void log(Level level, Supplier<String> msgSupplier)

   now you can call it as follows:

   logger.log(Level.FINER, () -> "Problem: " + generateDiagnostics());

   The log method internally executes the lambda passes as an argument only if the logger is of the right level.
   The internal implementation of the log method is along these lines:

   public void log(Level level, Supplier<String> msgSupplier) {
        if (logger.isLoggable(level)) {
            log(level, msgSupplier.get());
        }
   }

   What's the takeaway from the story? If you see yourself querying the state of an object (such as the state of the
   logger) many times in client code, only to call some method on this object with arguments (such as to log a
   message), consider introducing a new method that calls that method, passed as a lambda or method reference only
   after internally checking the state of the object. Your code will be more readable (less cluttered) and better
   encapsulated, without exposing the state of the object in client code.

   Execute Around
   If you find yourself surrounding different code with the same preparation and cleanup phases, you can often
   pull that code into a lambda. The benefit is that you can reuse the logic dealing with the preparation and
   cleanup phases, thus reducing code duplication.

   For example:
    */
    String oneLine =
            processFile(BufferedReader::readLine);
    String twoLines =
            processFile((BufferedReader b) -> b.readLine() + b.readLine());

    public static String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader((new FileReader("java-inaction/README.md")))) {
            return p.process(br);
        }
    }

    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }

   /*
   This code was made possible by introducing the function interface BufferedReaderProcessor, which lets you pass
   different lambdas to work with a BufferedReader object.
    */


    public static void main(String[] args) {
        r1.run();
        r2.run();
    }
}

