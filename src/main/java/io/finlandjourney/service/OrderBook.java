package io.finlandjourney.service;

import java.util.List;

import io.finlandjourney.model.*;

public interface OrderBook {

    /**
     * Adds an order to the order book. Attempts to match the order against opposing orders
     * (buy vs sell or sell vs buy). If a matching order is found, executes it.
     *
     * @param order the order to be added
     * @return the same order after processing
     */
    Order addOrder(Order order);

    /**
     * Retrieves the list of buy orders.
     *
     * @return a list of buy orders
     */
    List<Order> getBuyOrders();

    /**
     * Retrieves the list of sell orders.
     *
     * @return a list of sell orders
     */
    List<Order> getSellOrders();
}
