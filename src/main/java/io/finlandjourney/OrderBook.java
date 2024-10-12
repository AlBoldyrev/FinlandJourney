package io.finlandjourney;

import java.time.LocalDateTime;
import java.util.*;
import lombok.*;

@Data
public class OrderBook {

    TreeMap<OrderKey, List<Order>> buyOrders = new TreeMap<>(Comparator.reverseOrder());
    TreeMap<OrderKey, List<Order>> sellOrders = new TreeMap<>();

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

    private void processOrder(Order order, TreeMap<OrderKey, List<Order>> opposingOrders,
                              TreeMap<OrderKey, List<Order>> ownOrders, OrderKey orderKey, boolean isBuyOrder) {
        int quantity = order.getQuantity();
        StringBuilder logMessage = new StringBuilder();

        if (!opposingOrders.isEmpty()) {
            Iterator<Map.Entry<OrderKey, List<Order>>> iterator = opposingOrders.entrySet().iterator();
            List<String> processedOrders = new ArrayList<>();

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

                            logMessage.append("Updated ").append(isBuyOrder ? "SELL" : "BUY")
                                    .append(" order with id = '").append(opposingOrder.getOrderId())
                                    .append("'.  Remaining quantity = ").append(opposingOrder.getQuantity()).append(". ");

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

                        processedOrders.add("Bought " + quantityProcessed + " from " + (isBuyOrder ? "SELL" : "BUY") + " order with id = '" + opposingOrder.getOrderId() + "'");
                    }
                }
            }

            if (isBuyOrder && quantity == 0) {
                logMessage.insert(0, "Completed BUY order with id = '" + order.getOrderId() + "'. ");
                logMessage.append(String.join(", ", processedOrders)).append(".");
                System.out.println(logMessage);
            } else if (!isBuyOrder && quantity == 0) {
                logMessage.insert(0, "Completed SELL order with id = '" + order.getOrderId() + "'. ");
                logMessage.append(String.join(", ", processedOrders)).append(".");
                System.out.println(logMessage);
            }
        }

        if (quantity > 0) {
            order.setQuantity(quantity);
            ownOrders.computeIfAbsent(orderKey, k -> new ArrayList<>()).add(order);
            System.out.println((isBuyOrder ? "BUY" : "SELL") + " order with id = '" + order.getOrderId() + "' added to book with remaining quantity " + order.getQuantity() + ".");
        }
    }
}
