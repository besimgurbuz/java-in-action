package com.besimgurbuz.models;

/**
 * @author Besim Gurbuz
 */
public class Transaction {
    private final Trader trader;
    private final int year;
    private final int amount;

    public Transaction(Trader trader, int year, int amount) {
        this.trader = trader;
        this.year = year;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "{" + this.trader + ", " +
                "year: " + this.year + ", " +
                "value: " + this.amount + "}";
    }

    public Trader getTrader() {
        return trader;
    }

    public int getYear() {
        return year;
    }

    public int getAmount() {
        return amount;
    }
}
