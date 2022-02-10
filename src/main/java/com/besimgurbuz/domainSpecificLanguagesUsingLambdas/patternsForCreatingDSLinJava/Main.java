package com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava;

import com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava.domain.Order;
import com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava.domain.Stock;
import com.besimgurbuz.domainSpecificLanguagesUsingLambdas.patternsForCreatingDSLinJava.domain.Trade;

/*
    Orders domain model is straightforward. It's cumbersome to create objects representing orders, for example. Try
    to define a simple order that contains two trades for your customer BigBand:
 */
public class Main {
    public static void creatingAStockTradingOrderByUsingTheDomainObjectsApiDirectly() {
        Order order = new Order();
        order.setCustomer("BigBank");

        Trade trade1 = new Trade();
        trade1.setType(Trade.Type.BUY);

        Stock stock1 = new Stock();
        stock1.setSymbol("IBM");
        stock1.setMarket("NYSE");

        trade1.setStock(stock1);
        trade1.setPrice(125.00);
        trade1.setQuantity(80);
        order.addTrade(trade1);

        Trade trade2 = new Trade();
        trade2.setType(Trade.Type.BUY);

        Stock stock2 = new Stock();
        stock2.setSymbol("GOOGLE");
        stock2.setMarket("NASDAQ");

        trade2.setStock(stock2);
        trade2.setPrice(375.00);
        trade2.setQuantity(50);
        order.addTrade(trade2);

        /*
            The verbosity of this code is hardly acceptable; you can’t expect a non-developer
            domain expert to understand and validate it at first glance. What you need is a DSL
            that reflects the domain model and allows it to be manipulated in a more immediate,
            intuitive way. You can employ various approaches to achieve this result. In the rest of
            this section, you learn the pros and cons of these approaches.
         */
    }
}
