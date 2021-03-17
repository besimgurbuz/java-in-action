package com.besimgurbuz.dsl;

import com.besimgurbuz.models.Person;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class SmallDSLsInModernJavaAPIs {
    public static List<Person> persons = new ArrayList<>(List.of(
            new Person(10, "Jack"),
            new Person(16, "Tom"),
            new Person(21, "Danny"),
            new Person(32, "Rachel"),
            new Person(20, "Martha")
    ));
    public static void main(String[] args) throws IOException {
        // Old way
        Collections.sort(persons, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return p1.getAge() -  p2.getAge();
            }
        });

        // With Lambdas
        Collections.sort(persons, (p1, p2) -> p1.getAge() - p2.getAge());

        // With static Comparator.comparing
        Collections.sort(persons, comparing(p -> p.getAge()));

        // With name reference
        Collections.sort(persons, comparing(Person::getAge));

        Collections.sort(persons, comparing(Person::getAge).thenComparing(Person::getName));

        // new sort method
        persons.sort(comparing(Person::getAge).thenComparing(Person::getName));

        // This small API is minimal DSL for the domain of collection sorting.

        streamDSL();
    }

    public static void streamDSL() throws IOException {
        /*
        The Stream API seen as a DSL to manipulate collections

        The Stream interface is a great example of a small internal DSL introduced into the native
        Java API. In fact, a Stream can be seen as a compact but powerful DSL that filters, sorts,
        transforms, groups and manipulates the items of collection.
        Suppose that you’re required to read a log file and collect the first 40 lines, starting
        with the word “ERROR” in a List<String> . You could perform this task in an imperative
        style, as shown in the following listing.
         */
        List<String> errors = new ArrayList<>();
        int errorCount = 0;
        BufferedReader bufferedReader
                = new BufferedReader(new FileReader("./src/main/java/com/besimgurbuz/dsl/file.log"));
        String line = bufferedReader.readLine();
        while (errorCount < 40 && line != null) {
            if (line.startsWith("ERROR")) {
                errors.add(line);
                errorCount++;
            }
            line = bufferedReader.readLine();
        }
//        System.out.println(errors);

        /*

         */

        List<String> errors2 = Files.lines(Paths.get("./src/main/java/com/besimgurbuz/dsl/file.log"))
                .filter(l -> l.startsWith("ERROR"))
                .limit(40)
                .collect(Collectors.toList());
        System.out.println(errors2);
    }
}
