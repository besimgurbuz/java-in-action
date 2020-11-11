package com.besimgurbuz.collectionEnhancements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

public class WorkingWithMap {
    /*
    Java 8 introduces several default methods supported by the Map interface. (Default methods are
    covered in detail in chapter 13, but here you can think of them as being pre-implemented methods in
    an interface.) The purpose of these new operations is to help you write more concise code by using
    a readily available idiomatic pattern instead of implementing it yourself. We look at these
    operations in the following sections, starting with the shiny new forEach.

    forEach
    Iterating over the keys and values of a Map has traditionally been awkward. In fact, you needed to
    use an iterator of Map.Entry<K, V> over the entry set of a Map:
     */

    public static void main(String[] args) {
        Map<String, Integer> ageOfFriends = new HashMap<>(Map.of(
            "Besim", 22,
            "Semiha", 22,
            "Tugba", 26,
            "Havvanur", 22,
            "Henry", 35,
            "John", 45
        ));

        for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
            String friend = entry.getKey();
            Integer age = entry.getValue();
            System.out.printf("%s is %d years old%n", friend, age);
        }
        /*
        Since Java 8, the Map interface has supported the forEach method, which accepts a BiConsumer,
        taking the key and value as arguments. Using forEach makes your code more concise:
         */
        System.out.println("*********************************");
        ageOfFriends.forEach((name, age) ->
                System.out.printf("%s is %d years old%n", name, age));

        System.out.println("*********************************");
        /*
        A concern related to iterating over date is sorting it. Java 8 introduced a couple of
        convenient ways to compare entries in a Map.
         */
        /* Sorting
            Two new utilities let you sort the entries of a map by values or keys:
                - Entry.comparingByValue
                - Entry.comparingByKey

            The code:
        */
        Map<String, String> favouriteMovies = new HashMap<>(Map.of(
            "Raphael", "Star Wars",
            "Cristina", "Matrix",
            "Olivia", "James Bond",
            "Adam", "Lord of the Rings"
        ));

        favouriteMovies
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(System.out::println);

        /*
        Another common pattern is how to act when the key you're looking up in the Map isn't present.
        The new getOrDefault method can help.

        getOrDefault
            When the key you're looking up isn't present, you receive a null reference that you have to
            check against to prevent a NullPointerException. A common design style is to provide a
            default value instead. Now you can encode this idea more simply by using the getOrDefault
            method. This method takes the key as the first argument and a default value (to be used
            when the key is absent from the Map) as the second argument:
         */
        System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
        System.out.println(favouriteMovies.getOrDefault("Jack", "Matrix"));

        /*
        Note that if the key existed in the Map but was accidentally associated with a null
        value, getOrDefault can still return null. Also note that the expression you pass as a
        fallback is always evaluated, whether the key exists or not. Java 8 also included a few
        more advanced patterns to deal with the presence and absence of values for a given key.
        You will learn about these new methods in the next section.


         Compute patterns
         Sometimes, you want to perform an operation conditionally and store its result, depending
         on whether a key is present or absent in Map. You may want to cache the result of an
         expensive operation given a key, for example. If the key is present, there's no need to
         recalculate the result. Three new operations can help:

            - computeIfAbsent -- If there's no specified value for the given key (it's absent or its
            value is null), calculate a new value by using the key and add it to the Map.

            - computeIfPresent -- If the specified key is present, calculate a new value for it and
            add it to the Map.

            - compute -- This operation calculates a new value for a given key and stores it in the
            Map.

        One use of computeIfAbsent is for caching information. Suppose that you parse each line of a
        set of files and calculate their SHA-256 representation. If you've processed the data previ-
        ously, there's no need to recalculate it.

          Now suppose that you implement a cache by using a Map, and you use an instance of Message-
        Diggest to calculate SHA-256 hashes:

        Then you can iterate through the data and cache the results:
         */
        try (Stream<String> lines = Files.lines(Paths.get("/home/besim/develop/java-in-action/src/main/java/com/besimgurbuz/collectionEnhancements/test_file.txt"))) {
            Map<String, byte[]> dataToHash = new HashMap<>();
            lines.forEach(line -> dataToHash.computeIfAbsent(line, WorkingWithMap::calculateDigest));
            System.out.println(dataToHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nextBlock();
        nextBlock2();
    }

    static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    static private byte[] calculateDigest(String key) {
        return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
    }

    /*
    This pattern is also useful for conveniently dealing with maps that store multiple values. if you
    need to add an element to a Map<K, List<V>>, you need to ensure that the entry has been
    initialized. This pattern is a verbose one to put in place. Suppose that you want to build up a
    list of movies for your friend Marry:
     */
    public static void nextBlock() {
        Map<String, List<String>> friendMovies = new HashMap<>(Map.of(
                "Raphael", List.of("Jack Reacher 2"),
                "Martha", List.of("Street Fight", "Winner"),
                "Jack", List.of("The last fight", "Green way"),
                "Nelson", List.of("Avengers", "Lord of the Rings")
        ));
        String friend = "Marry";
        List<String> movies = friendMovies.get(friend);

        if (movies == null) {
            movies = new ArrayList<>();
            friendMovies.put(friend, movies);
        }

        movies.add("Star Wars");
        System.out.println(friendMovies);

        /*
        How can you use computeIfAbsent instead? It returns the calculated value after adding it to the
        Map if the key wasn't found; otherwise, it returns existing value, You can use it as follows:
         */
        friendMovies.computeIfAbsent("Ellen", name -> new ArrayList<>()).add("Star Wars");

        System.out.println(friendMovies);

        /*
        The computeIfPresent method calculates a new value if the current value associated with the
        key is present in the Map and non-null. Note a subtitle: if the function that produces the
        value returns null, the current mapping is removed from the Map. If you need to remove a
        mapping, however, an overloaded version of the remove method is better suited to the task.
        You learn about this method in the next section.

        Remove Patterns
        You already know about the remove method that lets you remove a Map entry for a given key.
        Since Java 8, an overloaded version removes an entry only if the key is associated with a
        specific value. Previously, this code is how you'd implement this behavior
         */

        String key = "Raphael";
        String value = "Jack Reacher 2";
        if (friendMovies.containsKey(key) &&
                Objects.equals(friendMovies.get(key), List.of(value))) {
            friendMovies.remove(key);
            System.out.println("deleted");
        } else {
            System.out.println("not deleted");
        }
        System.out.println(friendMovies);

        // Here is how you can do the same thing now, which you have to admit is much more to the
        // point
        String key2 = "Ellen";
        List<String> value2 = List.of("Star Wars");
        friendMovies.remove(key2, value2);
        System.out.println("recent: " + friendMovies);
    }

    /*
    Replacement patterns
    Map has two new methods that let you replace the entries inside a Map:
        - replaceAll -- Replaces each entry's value with the result of applying a BiFunction. This
        method works similarly to replaceAll on a List, which you saw earlier.

        - Replace -- Lets you replace a value in the Map if a key is present. An additional overload
        replaces the value only if it hte is mapped to a certain value.
     */
    public static void nextBlock2() {
        Map<String, String> favouriteMovies = new HashMap<>();
        favouriteMovies.put("Raphael", "Star Wars");
        favouriteMovies.put("Olivia", "james bond");
        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);

        /*
        The replace patterns you've learned work with a single Map. But what if you have to combine
        and replace values from two Maps? You can use a new merge method for that task.

        Merge
        Suppose that you'd like to merge two intermediate Maps, perhaps two separate Maps for two
        groups of contacts. You can use putAll as follows:
         */
        Map<String, String> family = Map.ofEntries(
                Map.entry("Teo", "Star Wars"),
                Map.entry("Cristina", "James Bond"));
        Map<String, String> friends = Map.ofEntries(
                Map.entry("Raphael", "Star Wars"),
                Map.entry("Cristina", "Remember Me"));

        Map<String, String> everyone = new HashMap<>(family);
        everyone.putAll(friends);
        System.out.println(everyone);

        /*
        This code works expected as long as you don't have duplicate keys. If you require more flex-
        ibility in how values are combines, you can use the new merge method. This method takes a
        BiFunction to merge values that have a duplicate key. Suppose that Cristine is in both the
        family and friends maps but with different associated movies:
         */
        Map<String, String> family2 = Map.ofEntries(
                Map.entry("Teo", "Star Wars"), Map.entry("Cristina", "James Bond"));
        Map<String, String> friends2 = Map.ofEntries(
                Map.entry("Raphael", "Star Wars"), Map.entry("Cristina", "Matrix"));

        /*
        Then you could use the merge method in combination with forEach to provide a way to deal
        with the conflict. The following code concatenates the string names of the two movies:
         */
        Map<String, String> everyone2 = new HashMap<>(family);
        friends2.forEach((k, v) ->
                everyone2.merge(k, v, (movie1, movie2) -> String.format("%s & %s", movie1, movie2)));

        System.out.println(everyone2);

        /*
        You can also use merge to implement initialization checks. Suppose that you have a Map for
        recording how many times a movie is watched. You need to check that the key representing the
        movie is in the map before you can increment its value:
         */

        Map<String, Long> moviesToCount = new HashMap<>();
        String movieName = "JamesBond";
        Long count = moviesToCount.get(movieName);

        if (count == null) {
            moviesToCount.put(movieName, 1L);
        } else {
            moviesToCount.put(movieName, count + 1L);
        }
        // With what i learned this section
//        moviesToCount.putIfAbsent(movieName, 1L);
//        moviesToCount.computeIfPresent(movieName, (k, v) -> v + 1L);

        // This code can be rewritten as:
        moviesToCount.merge(movieName, 1L, (k, c) -> c + 1L);

        System.out.println(moviesToCount);
    }

}
