package io.finlandjourney;

import java.time.*;

public class Main {
    public static void main(String[] args) {
        OrderBook orderBook = new OrderBookImpl();

        Order sellOrder = new Order("1", 10, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);
    }
}
