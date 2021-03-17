package com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava;

import com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava.domain.Order;
import com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava.domain.Stock;
import com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava.domain.Trade;

/*
    Method Chaining
    The first style of DSL to explore is one of the most common. It allows you to define a trading order with a
    single chain of method invocations. The following listing shows an example of this type of DSL.

        Order order = forCustomer("BigBank")
                        .buy(80)
                        .stock("IBM")
                            .on("NYSE")
                        .at(125.00)
                        .sell(50)
                        .stock("GOOGLE")
                            .on("NASDAQ")
                        .at(375.00)
                    .end();

    This code looks like a big improvement. It's very likely that your domain expert will understand this code
    effortlessly. But how can you implement a DSL to achieve this result? You need a few builders that create the
    objects of this domain through a fluent API. The top-level builder creates and wraps an order, making it
    possible to add one or more trades to it, as shown in the next listing.
 */
public class MethodChainingOrderBuilder {
    public final Order order = new Order();

    private MethodChainingOrderBuilder(String customer) {
        order.setCustomer(customer);
    }

    public static MethodChainingOrderBuilder forCustomer(String customer) {
        return new MethodChainingOrderBuilder(customer);
    }

    public TradeBuilder buy(int quantity) {
        return new TradeBuilder(this, Trade.Type.BUY, quantity);
    }

    public TradeBuilder sell(int quatity) {
        return new TradeBuilder(this, Trade.Type.SELL, quatity);
    }

    public MethodChainingOrderBuilder addTrade(Trade trade) {
        order.addTrade(trade);
        return this;
    }

    public Order end() {
        return order;
    }
}

class TradeBuilder {
    private final MethodChainingOrderBuilder builder;
    public final Trade trade = new Trade();

    TradeBuilder(MethodChainingOrderBuilder builder,
                 Trade.Type type, int quantity) {
        this.builder = builder;
        trade.setType(type);
        trade.setQuantity(quantity);
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(builder, trade, symbol);
    }
}

class StockBuilder {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    StockBuilder(MethodChainingOrderBuilder builder,
                 Trade trade, String symbol) {
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilderWithStock on(String market) {
        stock.setMarket(market);
        trade.setStock(stock);
        return new TradeBuilderWithStock(builder, trade);
    }
}

class TradeBuilderWithStock {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
        this.builder = builder;
        this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price) {
        trade.setPrice(price);
        return builder.addTrade(trade);
    }
}

class UsingMethodChaining {
    public static void main(String[] args) {
        Order order = MethodChainingOrderBuilder.forCustomer("BigBank")
                .buy(80)
                .stock("IBM")
                .on("NYSE")
                .at(125.00)
                .sell(50)
                .stock("GOOGLE")
                .on("NASDAQ")
                .at(375.00)
                .end();
        System.out.println(order.getCustomer());
        System.out.println(order.getValue());
    }
}
