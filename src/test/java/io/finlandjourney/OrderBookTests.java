package io.finlandjourney;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderBookTests {

    private OrderBook orderBook;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        orderBook = new OrderBookImpl();
    }

    @Test
    void testAddSellOrderWithoutMatchingBuyOrder() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, NOW);
        orderBook.addOrder(sellOrder);
        List<Order> sellOrders = orderBook.getSellOrders();

        assertNotNull(sellOrders);
        assertEquals(1, sellOrders.size());
        assertEquals(sellOrder, sellOrders.get(0));
    }

    @Test
    void testFullSellOrderExecution() {
        Order buyOrder = new Order("1", 5, 100, OrderType.BUY, NOW);
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL, NOW.plusSeconds(1));

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        assertTrue(orderBook.getSellOrders().isEmpty());
        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testFullBuyOrderExecution() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, NOW);
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, NOW.plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        assertTrue(orderBook.getSellOrders().isEmpty());
        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testPartialSellOrderExecution() {
        Order buyOrder = new Order("1", 3, 100, OrderType.BUY, NOW);
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL, NOW.plusSeconds(1));

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders();
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(2, remainingSellOrders.get(0).getQuantity());

        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testPartialBuyOrderExecution() {
        Order sellOrder = new Order("1", 3, 100, OrderType.SELL, NOW);
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, NOW.plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> remainingBuyOrders = orderBook.getBuyOrders();
        assertNotNull(remainingBuyOrders);
        assertEquals(1, remainingBuyOrders.size());
        assertEquals(2, remainingBuyOrders.get(0).getQuantity());

        assertTrue(orderBook.getSellOrders().isEmpty());
    }

    @Test
    void testAddSellOrderWithHigherPriceThanBuyOrder() {
        Order buyOrder = new Order("1", 5, 90, OrderType.BUY, NOW);
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL, NOW.plusSeconds(1));

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        List<Order> buyOrders = orderBook.getBuyOrders();
        List<Order> sellOrders = orderBook.getSellOrders();

        assertNotNull(buyOrders);
        assertNotNull(sellOrders);
        assertEquals(1, buyOrders.size());
        assertEquals(1, sellOrders.size());
    }

    @Test
    void testAddBuyOrderWithLowerPriceThanSellOrder() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, NOW);
        Order buyOrder = new Order("2", 5, 90, OrderType.BUY, NOW.plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> buyOrders = orderBook.getBuyOrders();
        List<Order> sellOrders = orderBook.getSellOrders();

        assertNotNull(buyOrders);
        assertNotNull(sellOrders);
        assertEquals(1, buyOrders.size());
        assertEquals(1, sellOrders.size());
    }

    @Test
    void testTwoSellOrdersWithSamePriceAndOneBuyOrder() {
        Order sellOrder1 = new Order("1", 5, 100, OrderType.SELL, NOW.plusSeconds(1));
        Order sellOrder2 = new Order("2", 5, 100, OrderType.SELL, NOW.plusSeconds(2));
        Order buyOrder = new Order("3", 5, 100, OrderType.BUY, NOW);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders();

        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(sellOrder2, remainingSellOrders.get(0));
        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testBuyOrderExecutesSellOrderWithEarlierCreationTime() {
        Order sellOrder1 = new Order("1", 5, 100, OrderType.SELL, NOW.plusSeconds(2));
        Order sellOrder2 = new Order("2", 5, 100, OrderType.SELL, NOW.plusSeconds(1));
        Order buyOrder = new Order("3", 5, 100, OrderType.BUY, NOW);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders();

        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(sellOrder1, remainingSellOrders.get(0));
        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testCompareToDifferentPrices() {
        OrderKey orderKey1 = new OrderKey(100, NOW);
        OrderKey orderKey2 = new OrderKey(90, NOW);

        assertTrue(orderKey1.compareTo(orderKey2) > 0);
        assertTrue(orderKey2.compareTo(orderKey1) < 0);
    }

    @Test
    void testPartialExecutionOfOpposingOrder() {
        Order sellOrder = new Order("1", 10, 100, OrderType.SELL, NOW);
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, NOW.plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders();
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(5, remainingSellOrders.get(0).getQuantity());

        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testOpposingOrderListBecomesEmpty() {
        Order sellOrder1 = new Order("1", 5, 100, OrderType.SELL, NOW);
        Order sellOrder2 = new Order("2", 5, 100, OrderType.SELL, NOW.plusSeconds(1));
        Order buyOrder = new Order("3", 10, 100, OrderType.BUY, NOW.plusSeconds(2));

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(buyOrder);

        assertTrue(orderBook.getSellOrders().isEmpty());
        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void testMainMethod() {
        String[] args = {};
        Main.main(args);
    }
}
