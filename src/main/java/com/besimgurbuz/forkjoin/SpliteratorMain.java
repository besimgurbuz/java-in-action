package com.besimgurbuz.forkjoin;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Besim Gurbuz
 */
public class SpliteratorMain {
    /**
     * The Spliterator is another new interface added to Java 8: its name stands for "split-able
     * iterator." Like Iterators, Spliterators are used to traverse the elements of a source, but
     * they're also designed to do this in parallel. Although you may not have to develop your own
     * Spliterator in practice, understanding how to do so will give you a wider understanding about
     * how parallel streams work. Java 8 already provides a default Spliterator implementation for all
     * the data structures included in its Collections Framework. The Collection interface now provides
     * a default method spliterator() (you will learn more about default methods in chapter 13) which
     * returns Spliterator object. The Spliterator interface defines several methods, as shown in the
     * following listing.
     *
         *  public interface Spliterator<T> {
         *      boolean tryAdvance(Consumer<? super T> action);
         *      Spliterator<T> trySplit();
         *      long estimateSize();
         *      int characteristics();
         *  }
     *
     * As usual, T is the type of the elements traversed by the Spliterator. The tryAdvance method
     * behaves  in  a  way  similar  to  a  normal  Iterator  in  the  sense  that  it’s  used  to
     * sequentially consume the elements of the Spliterator one by one, returning true if there are
     * still other elements to be traversed. But the trySplit method is more specific  to  the
     * Spliterator  interface  because  it’s  used  to  partition  off  some  of  its  elements to a
     * second Spliterator (the one returned by the method), allowing the two to be processed in
     * parallel. A Spliterator may also provide an estimation of the number of the elements remaining
     * to be traversed via its estimateSize method, because even  an  inaccurate  but
     * quick-to-compute  value  can  be  useful  to  split  the  structure more or less evenly.
     * It’s important to understand how this splitting process is performed internally in order to take
     * control of it when required. Therefore, we’ll analyze it in more detail in the next section.
     */

    // IMPLEMENTING YOUR OWN SPLITERATOR
    /*
    Let's look at a practical example of where you might need to implement your own Spliterator. We'll
    develop a simple method that counts the number of words in a String. An iterative version of this
    method could be written as shown in the following;
     */
    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else  {
                if (lastSpace) counter++;
                lastSpace = false;
            }
        }
        return counter;
    }

    final static String SENTENCE = " Nel   mezzo del cammin  di nostra  vita " +
            "mi  ritrovai in una  selva oscura" +
            " ché la  dritta via era   smarrita ";
    public static void main(String[] args) {
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
        System.out.println("Found " + countWords(stream) + " words: Stream<Character>");
    }

    /*
    Note that we added some additional random spaces in the sentence to demonstrate that the iterative
    implementation is working correctly even in the presence of multiple spaces between two words. As
    expected, this code prints out the following:

        Found 19 words

    Ideally you’d like to achieve the same result in a more functional style because this way you’ll be
    able, as shown previously, to parallelize this process using a parallel stream without having to
    explicitly deal with threads and their synchronization.

     REWRITING THE WordCounter IN FUNCTIONAL STYLE
     First, you need to convert the String into a stream. Unfortunately, there are primitive streams
     only for int, long, and double, so you’ll have to use a Stream<Character>:
     */
    static Stream<Character> stream = IntStream.range(0, SENTENCE.length())
            .mapToObj(SENTENCE::charAt);

    /*
    You can calculate the number of words by performing a reduction on this stream. While reducing the
    stream, you'll have to carry a state consisting of two variables: an int counting the number of
    words found so far and a boolean to remember if the last encountered Character was a space or not.
    Because Java doesn't have tuples (a construct to represent an ordered list of heterogeneous
    elements without the need of a wrapper object), you'll have to create a new class, WordCounter,
    which will encapsulate this state as shown in the following listing.
     */

    static class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        /**
         * Accumulate method traverses Characters one by one.
         * @param c
         * @return
         */
        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ?
                        this :
                        new WordCounter(counter, true);
            } else {
                return lastSpace ?
                        new WordCounter(counter + 1, false) :
                        this;
            }
        }

        /**
         * Combines two WordCounters by summing their counters.
         * @param wordCounter
         * @return
         */
        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }

    /*
    In this listing, the accumulate methods defines how to change the state of the WordCounter, or,
    more precisely, with which state to create a new WordCounter because it's an immutable class. This
    is important to understand. We are accumulating state with an immutable class specifically so that
    the process can be parallelized in the next step. The method accumulate is called whenever a new
    Character of the stream is traversed. In particular, as you did in the countWordsIteratively method
    in listing 7.4, the counter is incremented when a new non-space is met, and the last character
    encountered is a space.

    The second method, combine is invoked to aggregate the partial results of two WordCounters
    operating on two different sub-parts of the stream of Characters, so it combines two WordCounters
    by summing their internal counters.

    Now that you've encoded the logic of how to accumulate characters on a WordCounter and how to
    combine them in the WordCounter itself, writing method that will reduce the stream of Characters is
    straightforward:
     */

    public static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);
        return wordCounter.getCounter();
    }

    /*
    Making the WordCounter Work in Parallel
     */
}
