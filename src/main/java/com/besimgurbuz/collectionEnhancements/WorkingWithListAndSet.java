package com.besimgurbuz.collectionEnhancements;

import com.besimgurbuz.models.Dish;
import com.besimgurbuz.models.Transaction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Besim Gurbuz
 */
public class WorkingWithListAndSet {
    /*
    Java 8 introduced a couple of methods into the List and Set interfaces:
        - removeIf: removes element matching a predicate. It's available on all classes that implement
        List or Set (and is inherited from the Collection interface).
        - replaceAll: is available on List and replaces elements using a (UnaryOperator) function
        - sort is also available on the List interface and sorts the list itself.

    All these methods mutate the collections on which they're invoked. In other words, they change the
    collection itself, unlike stream operations, which produce a new (copied) result. Why would such
    methods be added? Modifying collections can be error-prone and verbose. So Java 8 added removeIf
    and replaceAll to help.
     */

    /* removeIf
    Consider the following code, which tries to remove dish that have a name starting with a digit:
     */
    static List<Dish> menu = new ArrayList<>(List.of(
            new Dish("0 burger", false, 400, Dish.Type.MEAT),
            new Dish("4 pizza", false, 500, Dish.Type.MEAT),
            new Dish("Pasta", false, 320, Dish.Type.MEAT),
            new Dish("fish and chips", false, 320, Dish.Type.FISH),
            new Dish("Zero calories burger", false, 100, Dish.Type.OTHER)
    ));

    public static void main(String[] args) {
        // THROWS ConcurrentModificationException
//        for (Dish dish : menu) {
//           if (Character.isDigit(dish.getName().charAt(0))) {
//               menu.remove(dish);
//           }
//        }
        /*
        Can you see the problem? Unfortunately, this code may result in a
        ConcurrentModificationException. Why? Under the hood, the for-each loop uses an Iterator object,
        so the code executed is as follows:
         */
        // THROWS ConcurrentModificationException
//        for (Iterator<Dish> iterator = menu.iterator();
//            iterator.hasNext(); ) {
//            Dish dish = iterator.next();
//
//            if (Character.isDigit(dish.getName().charAt(0))) {
//                menu.remove(dish); // problem we are iterating and modifying the collection through two separate objects
//            }
//        }
        /*
        Notice that two separate objects manage the collection:
            - The Iterator object, which is querying the source by using next() and hasNext()
            - The Collection object itself, which is removing the element by calling remove()

        As a result, the state of the iterator is no longer synced with the state of the collection, and
        vice versa. To solve this problem, you have to use the Iterator object explicitly and call its
        remove method:
         */
        // We can replace this with removeIf()
//        for (Iterator<Dish> iterator = menu.iterator();
//            iterator.hasNext(); ) {
//            Dish dish = iterator.next();
//            if (Character.isDigit(dish.getName().charAt(0))) {
//                iterator.remove();
//            }
//        }
        /*
        This code has become fairly verbose to write. This code pattern is now directly expressible
        with the Java 8 removeIf method, which is not only simpler but also protects you from these
        bugs. It takes a predicate indicating which elements to remove:
         */
        menu.removeIf(dish ->
                Character.isDigit(dish.getName().charAt(0)));
        System.out.println(menu);

        // replaceAll
        /*
        The replaceAll method on the List interface lets you replace each element in a list with a new
        one. Using the Streams API, you could solve this problem as follows:
         */
        List<String> referenceCodes = Arrays.asList("a12", "C14", "b13");

        referenceCodes.stream()
                .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
                .collect(Collectors.toList())
                .forEach(System.out::println);
        System.out.println("referenceCodes" + referenceCodes);
        /*
        This code results in a new collection of strings, however. You want a way to update the
        existing collection. You can use a ListIterator object as follows (supporting set() method
        to replace an element):
         */
        for (ListIterator<String> iterator = referenceCodes.listIterator();
            iterator.hasNext(); ) {
            String code = iterator.next();
            iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
        }
        System.out.println(referenceCodes);

        /*
        As you can see, this code is fairly verbose. In addition, as we explained earlier,
        using Iterator objects in conjunction with collection objects can be error-prone by
        mixing iteration and modification of the collection. In Java 8, you can simply write:
         */
        referenceCodes.replaceAll(code ->
                Character.toUpperCase(code.charAt(0)) + code.substring(1));
    }
}
