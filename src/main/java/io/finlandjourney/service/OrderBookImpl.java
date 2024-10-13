package io.finlandjourney.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import io.finlandjourney.model.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class OrderBookImpl implements OrderBook {

    private static final Logger logger = LoggerFactory.getLogger(OrderBookImpl.class);

    private final ConcurrentSkipListMap<OrderKey, List<Order>> buyOrders = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    private final ConcurrentSkipListMap<OrderKey, List<Order>> sellOrders = new ConcurrentSkipListMap<>();

    @Override
    public Order addOrder(Order order) {
        LocalDateTime creationTime = order.getCreationTime();
        OrderKey orderKey = new OrderKey(order.getPrice(), creationTime);

        if (order.getOrderType().equals(OrderType.BUY)) {
            processOrder(order, sellOrders, buyOrders, orderKey, true);
        } else if (order.getOrderType().equals(OrderType.SELL)) {
            processOrder(order, buyOrders, sellOrders, orderKey, false);
        }

        return order;
    }

    @Override
    public List<Order> getBuyOrders() {
        return getFlattenedOrderList(new ConcurrentSkipListMap<>(buyOrders));
    }

    @Override
    public List<Order> getSellOrders() {
        return getFlattenedOrderList(new ConcurrentSkipListMap<>(sellOrders));
    }

    private List<Order> getFlattenedOrderList(ConcurrentSkipListMap<OrderKey, List<Order>> ordersMap) {
        List<Order> allOrders = new ArrayList<>();
        for (List<Order> orders : ordersMap.values()) {
            allOrders.addAll(orders);
        }
        return Collections.unmodifiableList(allOrders);
    }

    private void processOrder(Order order, ConcurrentSkipListMap<OrderKey, List<Order>> opposingOrders,
                              ConcurrentSkipListMap<OrderKey, List<Order>> ownOrders, OrderKey orderKey, boolean isBuyOrder) {
        int quantity = order.getQuantity();

        if (!opposingOrders.isEmpty()) {
            Iterator<Map.Entry<OrderKey, List<Order>>> iterator = opposingOrders.entrySet().iterator();

            while (iterator.hasNext() && quantity > 0) {
                Map.Entry<OrderKey, List<Order>> entry = iterator.next();
                int opposingPrice = entry.getKey().getPrice();
                List<Order> opposingOrderList = entry.getValue();

                if ((isBuyOrder && opposingPrice <= order.getPrice()) || (!isBuyOrder && opposingPrice >= order.getPrice())) {
                    for (Iterator<Order> opposingIterator = opposingOrderList.iterator(); opposingIterator.hasNext() && quantity > 0;) {
                        Order opposingOrder = opposingIterator.next();
                        int opposingQuantity = opposingOrder.getQuantity();
                        int quantityProcessed;

                        if (opposingQuantity > quantity) {
                            quantityProcessed = quantity;
                            opposingOrder.setQuantity(opposingQuantity - quantity);

                            logger.info("Updated {} order with id = '{}'. Remaining quantity = {}.",
                                    isBuyOrder ? "SELL" : "BUY",
                                    opposingOrder.getOrderId(),
                                    opposingOrder.getQuantity());

                            quantity = 0;
                        } else if (opposingQuantity == quantity) {
                            quantityProcessed = quantity;
                            opposingIterator.remove();
                            if (opposingOrderList.isEmpty()) {
                                iterator.remove();
                            }
                            quantity = 0;
                        } else {
                            quantityProcessed = opposingQuantity;
                            opposingIterator.remove();
                            if (opposingOrderList.isEmpty()) {
                                iterator.remove();
                            }
                            quantity -= opposingQuantity;
                        }

                        logger.info("Processed {} from {} order with id = '{}'",
                                quantityProcessed,
                                isBuyOrder ? "SELL" : "BUY",
                                opposingOrder.getOrderId());
                    }
                }
            }

            if (isBuyOrder && quantity == 0) {
                logger.info("Completed BUY order with id = '{}'.", order.getOrderId());
            } else if (!isBuyOrder && quantity == 0) {
                logger.info("Completed SELL order with id = '{}'.", order.getOrderId());
            }
        }

        if (quantity > 0) {
            order.setQuantity(quantity);
            ownOrders.computeIfAbsent(orderKey, k -> new ArrayList<>()).add(order);
            logger.info("{} order with id = '{}' added to book with remaining quantity {}.",
                    isBuyOrder ? "BUY" : "SELL",
                    order.getOrderId(),
                    order.getQuantity());
        }
    }
}
