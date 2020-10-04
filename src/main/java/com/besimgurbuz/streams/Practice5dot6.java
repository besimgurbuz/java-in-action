package com.besimgurbuz.streams;

import com.besimgurbuz.models.Trader;
import com.besimgurbuz.models.Transaction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Besim Gurbuz
 */
public class Practice5dot6 {
    /*
    1 Find all transactions in the year 2011 and sort them by value (small to high).
    2 What are all the unique cities where the traders work?
    3 Find all traders from Cambridge and sort them by name.
    4 Return a string of all traders’ names sorted alphabetically.
    5 Are any traders based in Milan?
    6 Print the values of all transactions from the traders living in Cambridge.
    7 What’s the highest value of all the transactions?
    8 Find the transaction with the smallest value.
     */

    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        // 1
        List<Transaction> answer1 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011)
                .sorted(Comparator.comparingInt(Transaction::getAmount))
                .collect(Collectors.toList());
        answer1.forEach(System.out::println);

        // 2
        List<String> answer2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .distinct()
                .collect(Collectors.toList());
        System.out.println(answer2);

        // 3
        List<Trader> answer3 = transactions.stream()
                .map(transaction -> transaction.getTrader())
                .filter(trader -> trader.getCity().equals("Cambridge"))
                .distinct()
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());
        System.out.println(answer3);

        // 4
        String answer4 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
        System.out.println(answer4);

        // 5
        boolean answer5 = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan"));

        if (answer5) {
            System.out.println("Found trader who based in Milan");
        }

        // 6
        transactions.stream()
                .filter(transaction -> transaction.getTrader().getCity().equals("Cambridge"))
                .forEach(transaction -> System.out.println(transaction.getAmount()));

        // 7
        int answer7 = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(0, Integer::max);
        System.out.println("Highest transaction: " + answer7);

        // 8
        Optional<Integer> answer8 = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(Integer::min);
        answer8.ifPresent(val -> System.out.println("Lowest transaction value: " + val));

    }
}
