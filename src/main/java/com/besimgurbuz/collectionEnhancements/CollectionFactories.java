package com.besimgurbuz.collectionEnhancements;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class CollectionFactories {
    /*
      Java 9 introduced a few convenient ways to create small collection objects. First, we'll review
      why programmers needed a better way to do things; then we'll show you how to use the new factory
      methods.

      How would you create a small list of elements in Java? You might want to group the names of you
      friends who are going on a holiday, for example. Here's one way:
     */

    public static void main(String[] args) {
        List<String> friends = new ArrayList<>();
        friends.add("Raphael");
        friends.add("Olivia");
        friends.add("Thibaut");

        /*
        But that's quite a few lines to write for storing three strings! A more convenient way to write
        this code is to use the Arrays.asList() factory method:
         */
        List<String> friends2 =
                Arrays.asList("Raphael", "Olivia", "Thibaut");

        /*
        You get a fixed-sized list that you can update, but not add elements to or remove elements from.
        Attempting to add elements, for example, results in an Unsupported-ModificationException, but
        updating by using the method set is allowed:
         */
        friends2.set(0, "Richard");
//        friends2.add("Marry"); throws UnsupportedOperationException

        /*
        This behavior seems slightly surprising because the underlying list is backed by a mutable array
        of fixed size.

        How about a Set? Unfortunately, there's no Arrays.asSet() factory method, so you need another
        trick. You can use the HashSet constructor, which accepts a List:
         */
        Set<String> friendsSet =
                new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));

        // Alternatively you could use the Streams API:
        Set<String> friendsSet2 =
                Stream.of("Raphael", "Olivia", "Thibaut").collect(Collectors.toSet());

        /*
        Both solutions, however, are far from elegant and involve unnecessary object allocations behind
        the scenes. Also note that you get a mutable Set as a result.
         */
        friendsSet2.add("Marry"); // friendsSet2 is mutable!

        /*
        How about Map? There’s no elegant way of creating small maps, but don’t worry; Java 9 added
        factory methods to make your life simpler when you need to create small lists, sets, and maps.
        We begin the tour of new ways of creating collections in Java by showing you what’s new with
        Lists.
         */
        /* Collection Literals
          Some languages, including Python and Groovy, support collection literals, which let you
          create collections by using special syntax, such as [42, 1, 5] to create a list of three
          numbers. Java doesn't provide syntactic support because language changes come with a high
          maintenance cost and restrict future use of possible syntax. Instead, Java 9 adds support by
          enhancing the Collection API.
         */


        // List Factory
        List<String> names = List.of("Murat", "Ahmet", "Mustafa");
        System.out.println(names);
        /*
        You'll notice something strange, however. Try to add an element to your list of names:
         */
        // names.add("Fatma");
        // Mutable One
        List<String> namesMutable = new ArrayList<>(List.of("Murat", "Ahmet", "Besim"));
        namesMutable.add("this is mutable");

        /*
        Running this code results in a java.lang.UnsupportedOperationException. In fact,the list that’s
        produced is immutable. Replacing an item with the set() method throws a  similar  exception.
        You  won’t  be  able  to  modify  it  by  using  the  set  method  either.This restriction is a
        good thing, however, as it protects you from unwanted mutations of  the  collections.  Nothing
        is  stopping  you  from  having  elements  that  are  mutable themselves. If you need a mutable
        list, you can still instantiate one manually. Finally, note that to prevent unexpected bugs and
        enable a more-compact internal representation, null elements are disallowed.
         */

        // Set factory
        Set<String> namesSet = Set.of("Rapheal", "Olivia", "Thibaut");
        System.out.println(namesSet);
//        namesSet.add("new one"); throws exception

        // If you try to create a Set by providing a duplicated element, you receive an IllegalArgumentException
//        Set<String> invalidSet = Set.of("Duplicated", "Duplicated"); throws exception


        /*
        Map factories
        Creating map is a bit complicate than creating lists and sets because you have to include both
        the key and the value. You have two ways to initialize an immutable map in Java 9. You can use
        the factory method Map.of, which alternates between keys and values:
         */
        Map<String, Integer> ageOfFriends =
                Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
        System.out.println(ageOfFriends);
        /*
        This method is convenient if you want to create a small map of up to ten keys and values. To go
        beyond this, use the alternative factory method called Map.ofEntries, which takes Map.Entry<K,
        V> objects but implemented with varargs. This method requires additional object allocations to
        wrap up a key and a value:
         */
        Map<String, Integer> agesWithEntries =
                Map.ofEntries(
                        Map.entry("Raphael", 30),
                        Map.entry("Olivia", 25),
                        Map.entry("Besim", 22)
                );
        System.out.println(agesWithEntries);

    }

}
