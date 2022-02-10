package com.besimgurbuz.refactoringTestingAndDebugging.testingLambda;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TestingLambda {
   /*
    Generally, good software engineering practice involves using unit testing to ensure
   that your program behaves as intended. You write test cases, which assert that small
   individual parts of your source code are producing the expected results. Consider a
   simple Point class for a graphical application:

   public class Point {
      private final int x;
      private final int y;
      private Point(int x, int y) {
         this.x = x;
         this.y = y;
      }
      public int getX() { return x; }
      public int getY() { return y; }
      public Point moveRightBy(int x) {
         return new Point(this.x + x, this.y);
      }
   }
   The following unit test checks whether the method moveRightBy behaves as expected:
    */
   @Test
   public void testMoveRightBy() throws Exception {
      Point p1 = new Point(5, 5);
      Point p2 = p1.moveRightBy(10);
      assertEquals(15, p2.getX());
      assertEquals(5, p2.getY());
   }

   /*
   Testing the behavior of a visible lambda

   Above code works because the moveByRight method is public, therefore, can be tested inside the test case. But
   lambdas don't have names (they're anonymous functions, after all), and testing them in you code is tricky.

   Sometimes, you have access to a lambda via a field so that you can reuse it, and you'd like to test the logic
   encapsulated in that lambda. You could test the lambda as you do when calling methods. For example:

   public class Point {
      public final static Comparator<Point> compareByXAndThenY =
         comparing(Point::getX).thenComparing(Point::getY);
      ...
   }
    */

   @Test
   public void testComparingTwoPoints() throws Exception {
      Point p1 = new Point(10, 15);
      Point p2 = new Point(10, 20);
      int result = Point.compareByXAndThenY.compare(p1, p2);
      assertTrue(result < 0);
   }

   /*
   Focusing on the behavior of the method using a lambda

   But the purpose of lambdas is to encapsulate a one-off piece of behavior to be used by another method. In that
   case, you shouldn't make lambda expressions available publicly; they're only implementation details. You should
   test the behavior of the lambda expression for example let's add moveAllPointsRightBy method to Point:

   public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
      return points.stream()
                   .map(p -> new Point(p.getX() + x, p.getY()))
                   .collect(Collectors.toList());
   }

   There’s no point (pun intended) in testing the lambda p -> new Point(p.getX() + x, p.getY()) ; it’s only an
   implementation detail for the moveAllPointsRightBy method. Instead, you should focus on testing the behavior of
   the moveAllPointsRightBy method:
    */
   @Test
   public void testMoveAllPointsRightBy() throws Exception {
      List<Point> points =
              Arrays.asList(new Point(5, 5), new Point(10, 5));
      List<Point> expectedPoints =
              Arrays.asList(new Point(15, 5), new Point(20, 5));

      List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
      assertEquals(expectedPoints, newPoints);
   }

   /*
   Pulling complex lambdas into separate methods

   Perhaps you come across a really complicated lambda expression that contains a lot of logic (such as a technical
   pricing algorithm with corner cases). What do you do, because you can't refer to the lambda expression inside
   you test? One strategy is to convert the lambda expression to a method reference (which involves declaring a new
   regular method). Then you can test the behavior of the new method as you would that of any regular method.

   Testing high-order functions

   Methods that take a function as an argument or return another function (so-called higher-order functions) are a
   little harder to deal with. One thing you can do if a method takes a lambda as an argument is test its behavior
   with different lambdas for example:
   let's create a filter method which takes an lambda and filters the list
    */
   public static List<Integer> filter(List<Integer> numbers, Predicate<Integer> predicate) {
      return numbers.stream().filter(predicate).collect(Collectors.toList());
   }

   @Test
   public void testFilter() throws Exception {
      List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
      List<Integer> even = filter(numbers, i -> i % 2 == 0);
      List<Integer> smallerThanThree = filter(numbers, i -> i < 3);

      assertEquals(Arrays.asList(2, 4), even);
      assertEquals(Arrays.asList(1, 2), smallerThanThree);
   }
}
