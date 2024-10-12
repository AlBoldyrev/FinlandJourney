Order Book

This project implements a simple order book for handling buy and sell orders of a single stock. The order book supports the following features:

	•	Price-based sorting: Orders are sorted by price using a TreeMap.
	•	FIFO for same-price orders: Orders with the same price are processed in a First-In-First-Out (FIFO) manner, based on the order’s creation time.
	•	Automatic order matching: Buy and sell orders are matched automatically, and quantities are updated as orders are partially or fully executed.
	•	Logging: Actions such as adding, matching, and updating orders are logged to the console.

Key Concepts

Classes

	1.	Order: Represents a buy or sell order, with the following properties:
	•	orderId: Unique ID for the order.
	•	quantity: Number of shares to buy or sell.
	•	price: Price per share.
	•	orderType: Indicates whether the order is a BUY or SELL order.
	•	creationTime: The timestamp when the order was created.
	2.	OrderKey: Used as a key in the TreeMap to sort orders by price and creation time. It implements Comparable<OrderKey>, which ensures:
	•	Orders are first sorted by price.
	•	If two orders have the same price, they are sorted by their creation time (earlier orders are processed first).
	3.	OrderBook: Manages the order book by storing and processing buy and sell orders. It uses two TreeMap<OrderKey, List<Order>> objects:
	•	buyOrders: Stores buy orders, sorted in reverse order (highest price first).
	•	sellOrders: Stores sell orders, sorted in natural order (lowest price first).

Functionality

	•	Adding Orders: When an order is added, it attempts to match it against existing orders from the opposing side (buy orders match against sell orders and vice versa). Orders are processed by price and creation time.
	•	Order Matching: If a matching order is found:
	•	If the opposing order has more quantity than the incoming order, the opposing order’s quantity is reduced.
	•	If the opposing order has the same or less quantity, it is fully executed and removed.
	•	Partial executions are supported, and any remaining quantity is kept in the order book.
	•	Logging: Every operation, including partial order execution, full execution, and adding new orders, is logged with detailed information.

Example Workflow

	1.	Add a SELL order for 10 shares at $100.
	2.	Add another SELL order for 5 shares at $100, created later than the first order.
	3.	Add a BUY order for 8 shares at $100.
	4.	The system will automatically match the BUY order with the first SELL order (FIFO), reducing the first SELL order’s quantity to 2 and completing the BUY order.
