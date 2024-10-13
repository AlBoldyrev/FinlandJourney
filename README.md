# OrderBook System

## Overview

The OrderBook system is designed to manage buy and sell orders in a marketplace. It efficiently handles order matching based on price and order creation time, allowing for both partial and full execution of orders. The system is thread-safe and uses `ConcurrentSkipListMap` for managing orders, ensuring performance and safety in multi-threaded environments.

## Features

- **Support for BUY and SELL orders**: The system processes both types of orders.
- **Order matching based on price and time (FIFO)**: Orders with better prices are prioritized, and if multiple orders have the same price, they are executed based on the time they were placed.
- **Partial and full order execution**: Orders can be partially filled if there isn’t enough volume on the opposing side.
- **Thread-safe order management**: Using `ConcurrentSkipListMap` ensures that the system is thread-safe and can process orders concurrently.
- **Detailed logging**: Uses SLF4J for logging order activity such as processing, completion, and updates.

## Design

The `OrderBook` interface provides methods for adding and retrieving orders, and the implementation is handled by the `OrderBookImpl` class.

### Key Classes:

- `OrderBook`: Interface for interacting with the order book.
- `OrderBookImpl`: Implements `OrderBook` and contains the core logic for order processing.
- `Order`: Represents a BUY or SELL order.
- `OrderKey`: Represents the key for sorting orders in the order book, based on price and creation time.
- `OrderType`: Enum for defining BUY and SELL orders.

## Methods

### `OrderBook` Interface:

- `Order addOrder(Order order)`: Adds a new order to the book. It processes the order by matching it against existing opposing orders (if available) or adds it to the appropriate buy or sell order list.
- `List<Order> getBuyOrders()`: Returns a list of current buy orders.
- `List<Order> getSellOrders()`: Returns a list of current sell orders.

### `OrderBookImpl` Class:

- **Thread-Safe**: The class uses `ConcurrentSkipListMap` to store orders, ensuring that operations can be performed safely in a multi-threaded environment without explicit locking.

## Order Execution Logic

1. **Order Matching**:
    - **BUY orders**: Matched against SELL orders with the same or lower price.
    - **SELL orders**: Matched against BUY orders with the same or higher price.
    - Orders with the same price are executed in the order of their creation time (FIFO).

2. **Partial Execution**:
    - If an order cannot be fully executed due to insufficient quantity in opposing orders, it is partially executed, and the remaining quantity is added back to the book.

3. **Order Removal**:
    - Once an order is fully executed, it is removed from the book.
    - If the last order in a price bucket is executed, the entire bucket is removed.
