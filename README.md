# OrderBook System

## Overview

The **OrderBook** system is designed to manage buy and sell orders in a marketplace. The system matches buy orders against sell orders based on price and order creation time, allowing partial or full execution of orders.

## Features

- Supports **BUY** and **SELL** orders.
- Orders are processed based on price priority and creation time (**FIFO**).
- Uses a custom comparator for sorting orders.
- Thread-safe design (if needed in future).
- Detailed logging of order processing using **SLF4J**.

## Interface

The `OrderBook` interface provides methods for adding and retrieving buy and sell orders. The implementation is handled by the `OrderBookImpl` class.

### Methods

- **`Order addOrder(Order order)`**: Adds an order to the order book, either executing it fully/partially against opposing orders or adding it to the appropriate list.
- **`List<Order> getBuyOrders()`**: Retrieves a list of all current buy orders.
- **`List<Order> getSellOrders()`**: Retrieves a list of all current sell orders.

## Order Execution Logic

1. **Order Matching**:
    - BUY orders are matched against SELL orders with the same or lower price.
    - SELL orders are matched against BUY orders with the same or higher price.
    - Orders with the same price are executed in the order of their creation time (oldest first).

2. **Partial Execution**:
    - If an order cannot be fully executed due to insufficient quantity in the opposing order(s), it is partially executed, and the remaining quantity is added back to the order book.

3. **Order Removal**:
    - Once an order is fully executed, it is removed from the book.
    - If the last order in a price bucket is fully executed, the entire bucket is removed from the book.
