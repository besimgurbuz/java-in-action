package com.besimgurbuz.forkjoin;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    You  could  try  to  speed  up  the  word-counting  operation  using  a  parallel  stream,
    as follows:
            System.out.println("Found " + countWords(stream.parallel()) + " words");

    Unfortunately, this time the output is
            Found 25 words

    Evidently something has gone wrong, but what? The problem isn’t hard to discover.Because the
    original String is split at arbitrary positions, sometimes a word is divided in  two  and  then
    counted  twice.  In  general,  this  demonstrates  that  going  from  a sequential  stream  to  a
    parallel  one  can  lead  to  a  wrong  result  if  this  result  may  be affected by the position
    where the stream is split. How can you fix this issue? The solution consists of ensuring that the
    String isn’t split at a random position but only at the end of a word. To do this, you’ll have to
    implement a Spliterator of Character that splits a String only between two words(as shown in the
    following listing) and then creates the parallel stream from it.
     */

    static class WordCounterSpliterator implements Spliterator<Character> {
        private final String string;
        private int currentChar = 0;

        WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            action.accept(string.charAt(currentChar++));
            // returns true if there are further characters to be consumed
            return currentChar < string.length();
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                // returns null to signal that the String to be parsed is small enough to be processed sequentially
                return null;
            }
            // Sets the candidate split position to be half of the string to be parsed
            for (int splitPos = currentSize / 2 + currentChar;
                    splitPos < string.length(); splitPos++) {
                if (Character.isWhitespace((string.charAt(splitPos)))) { // advances the split position until the next space
                    Spliterator<Character> spliterator =
                            new WordCounterSpliterator(string.substring(currentChar, splitPos));
                    currentChar = splitPos;
                    return spliterator;
                }
            }
            return null;
        }

        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }

    /*
    This Spliterator is created from the String to be parsed and iterates over its Characters by
    holding the index of the one currently being traversed. Let's quickly revisit the methods of the
    WordCounterSpliterator implementing the Spliterator interface:

        - The tryAdvance method feeds the Consumer with the Character in the String at the current
        index position and increments this position. The Consumer passed as its argument is an internal
        Java class forwarding the consumed Character to the set of functions that have to be applied it
        while traversing the stream, which this case only a reduction function, namely the accumulate
        method of the WordCounter class. The tryAdvance method returns true if the new cursor position
        is less than the total String length and there are further Characters to be iterated.

        - The trySplit method is the most important one in a Spliterator, because it’s the one
        defining the logic used to split the data structure to be iterated. As you did  in  the
        compute  method  of  the  RecursiveTask  implemented  in  listing  7.1,the first thing you
        have to do here is set a limit under which you don’t want to perform further splits. Here, you
        use a low limit of 10 Characters only to make sure that your program will perform some splits
        with the relatively short String you’re parsing. But in real-world applications you’ll have to
        use a higher limit, as  you  did  in  the  fork/join  example,  to  avoid  creating  too
        many  tasks.  If  the number of remaining Characters to be traversed is under this limit, you
        return null to signal that no further split is necessary. Conversely, if you need to per-form
        a split, you set the candidate split position to the half of the String chunk remaining to be
        parsed.  But  you  don’t  use  this  split  position  directly  because you want to avoid
        splitting in the middle of a word, so you move forward until you find a blank Character. Once
        you find an opportune split position, you create  a  new  Spliterator  that  will  traverse
        the  substring  chunk  going  from  the current position to the split one; you set the current
        position of this to the split one, because the part before it will be managed by the new
        Spliterator, and then you return it.

        - The estimatedSize of elements still to be traversed is the difference between the total
        length of the String parsed by this Spliterator and the position currently iterated.

        - Finally,  the  characteristics  method  signals  to  the  framework  that  thisSpliterator
        is  ORDERED  (the  order  is  the  sequence  of  Characters  in  theString), SIZED (the value
        returned by the estimatedSize  method  is  exact), SUBSIZED (the other Spliterators created by
        the trySplit method also have an exact size), NON-NULL (there can be no nullCharacters in the
        String), andIMMUTABLE  (no  further  Characters  can  be  added  while  parsing  the
        String because the String itself is an immutable class).
     */

    static Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
    static Stream<Character> stream2 = StreamSupport.stream(spliterator, true);
    /*
    The second boolean argument passed to the StreamSupport.stream factory method means that you want
    to create a parallel stream. Passing this parallel stream to the countWords method produces the
    correct output, as expected:

        Found 19 words

    You've seen how a Spliterator can let you to gain control over the policy used to split a data
    structure. One last notable feature of Spliterators is the possibility of binding the source of
    the elements to be traversed at the point of first traversal, first split, or first query for
    estimated size, rather than at the time of its creation. When this happens, it's called a
    late-binding Spliterator. We've dedicated appendix C to showing how you can develop a utility
    class capable of performing multiple operations on the same stream in parallel using this feature.
     */

    public static void main(String[] args) {
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
        System.out.println("Found " + countWords(stream) + " words: Stream<Character>");
        System.out.println("Found " + countWords(stream2) + " words: ParallelStream<Character>");
    }
}
