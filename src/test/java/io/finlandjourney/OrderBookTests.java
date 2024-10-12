package io.finlandjourney;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderBookTests {

    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
    }

    @Test
    void testAddSellOrderWithoutMatchingBuyOrder() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL);
        orderBook.addOrder(sellOrder);
        List<Order> sellOrders = orderBook.getSellOrders().get(100);

        assertNotNull(sellOrders);
        assertEquals(1, sellOrders.size());
        assertEquals(sellOrder, sellOrders.get(0));
    }

    @Test
    void testFullSellOrderExecution() {
        Order buyOrder = new Order("1", 5, 100, OrderType.BUY);
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        assertNull(orderBook.getSellOrders().get(100));
        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testFullBuyOrderExecution() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL);
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        assertNull(orderBook.getSellOrders().get(100));
        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testPartialSellOrderExecution() {
        Order buyOrder = new Order("1", 3, 100, OrderType.BUY);
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(100);
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(2, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testPartialBuyOrderExecution() {
        Order sellOrder = new Order("1", 3, 100, OrderType.SELL);
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> remainingBuyOrders = orderBook.getBuyOrders().get(100);
        assertNotNull(remainingBuyOrders);
        assertEquals(1, remainingBuyOrders.size());
        assertEquals(2, remainingBuyOrders.get(0).getQuantity());

        assertNull(orderBook.getSellOrders().get(100));
    }

    @Test
    void testAddSellOrderWithHigherPriceThanBuyOrder() {
        Order buyOrder = new Order("1", 5, 90, OrderType.BUY);
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        List<Order> buyOrders = orderBook.getBuyOrders().get(90);
        List<Order> sellOrders = orderBook.getSellOrders().get(100);

        assertNotNull(buyOrders);
        assertNotNull(sellOrders);
        assertEquals(1, buyOrders.size());
        assertEquals(1, sellOrders.size());
    }

    @Test
    void testAddBuyOrderWithLowerPriceThanSellOrder() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL);
        Order buyOrder = new Order("2", 5, 90, OrderType.BUY);

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> buyOrders = orderBook.getBuyOrders().get(90);
        List<Order> sellOrders = orderBook.getSellOrders().get(100);

        assertNotNull(buyOrders);
        assertNotNull(sellOrders);
        assertEquals(1, buyOrders.size());
        assertEquals(1, sellOrders.size());
    }

    @Test
    void testPartialBuyOrderWithRemainingSellOrder() {
        Order sellOrder = new Order("1", 10, 100, OrderType.SELL);
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(100);
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(5, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testIteratorContinuesButQuantityBecomesZero() {
        Order sellOrder1 = new Order("1", 3, 100, OrderType.SELL);
        Order sellOrder2 = new Order("2", 5, 100, OrderType.SELL);
        Order buyOrder = new Order("3", 3, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(100);
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(5, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testOpposingOrderListNotEmptyAfterPartialExecution() {
        Order sellOrder1 = new Order("1", 3, 100, OrderType.SELL);
        Order sellOrder2 = new Order("2", 3, 100, OrderType.SELL);
        Order sellOrder3 = new Order("3", 3, 100, OrderType.SELL);
        Order buyOrder = new Order("4", 5, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(100);
        assertNotNull(remainingSellOrders);
        assertEquals(2, remainingSellOrders.size());
        assertEquals(1, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testQuantityBecomesZeroDuringIteration() {
        Order sellOrder1 = new Order("1", 3, 100, OrderType.SELL);
        Order sellOrder2 = new Order("2", 3, 100, OrderType.SELL);
        Order sellOrder3 = new Order("3", 5, 100, OrderType.SELL);
        Order buyOrder = new Order("4", 6, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(100);
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(5, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(100));
    }

    @Test
    void testBuyOrderExecutionWithMultipleSellOrders() {
        Order sellOrder1 = new Order("1", 3, 80, OrderType.SELL);
        Order sellOrder2 = new Order("2", 2, 90, OrderType.SELL);
        Order sellOrder3 = new Order("3", 5, 110, OrderType.SELL);
        Order buyOrder = new Order("4", 6, 100, OrderType.BUY);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(110);
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(5, remainingSellOrders.get(0).getQuantity());

        List<Order> remainingBuyOrders = orderBook.getBuyOrders().get(100);
        assertNotNull(remainingBuyOrders);
        assertEquals(1, remainingBuyOrders.size());
        assertEquals(1, remainingBuyOrders.get(0).getQuantity());

        assertNull(orderBook.getSellOrders().get(80));
        assertNull(orderBook.getSellOrders().get(90));
    }

    @Test
    void testMainMethod() {
        String[] args = {};
        Main.main(args);
    }
}
