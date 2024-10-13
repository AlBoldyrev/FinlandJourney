package io.finlandjourney;

import java.time.*;

import io.finlandjourney.model.*;
import io.finlandjourney.service.*;

public class Main {
    public static void main(String[] args) {
        OrderBook orderBook = new OrderBookImpl();

        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 10, 100, OrderType.BUY, LocalDateTime.now());
        Order sellOrder2 = new Order("3", 5, 100, OrderType.SELL, LocalDateTime.now());

        orderBook.addOrder(sellOrder);

        Thread thread1 = new Thread(() -> {
            orderBook.addOrder(buyOrder);
            System.out.println("Added buyOrder in Thread 1");
        });

        Thread thread2 = new Thread(() -> {
            orderBook.addOrder(sellOrder2);
            System.out.println("Added sellOrder2 in Thread 2");
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final Buy Orders: " + orderBook.getBuyOrders());
        System.out.println("Final Sell Orders: " + orderBook.getSellOrders());
    }
}
