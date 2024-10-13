package io.finlandjourney;

import java.time.*;

import io.finlandjourney.model.*;
import io.finlandjourney.service.*;
import org.slf4j.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        OrderBook orderBook = new OrderBookImpl();

        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 10, 100, OrderType.BUY, LocalDateTime.now());
        Order sellOrder2 = new Order("3", 5, 100, OrderType.SELL, LocalDateTime.now());

        orderBook.addOrder(sellOrder);

        Thread thread1 = new Thread(() -> {
            orderBook.addOrder(buyOrder);
            logger.info("Added buyOrder in Thread 1");
        });

        Thread thread2 = new Thread(() -> {
            orderBook.addOrder(sellOrder2);
            logger.info("Added sellOrder2 in Thread 2");
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        logger.info("Final Buy Orders: {}", orderBook.getBuyOrders());
        logger.info("Final Sell Orders: {}", orderBook.getSellOrders());
    }
}
