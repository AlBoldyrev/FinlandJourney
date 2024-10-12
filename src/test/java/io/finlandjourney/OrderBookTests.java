package io.finlandjourney;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now());
        orderBook.addOrder(sellOrder);
        List<Order> sellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime()));

        assertNotNull(sellOrders);
        assertEquals(1, sellOrders.size());
        assertEquals(sellOrder, sellOrders.get(0));
    }

    @Test
    void testFullSellOrderExecution() {
        Order buyOrder = new Order("1", 5, 100, OrderType.BUY, LocalDateTime.now());
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        assertNull(orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime())));
        assertNull(orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime())));
    }

    @Test
    void testFullBuyOrderExecution() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        assertNull(orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime())));
        assertNull(orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime())));
    }

    @Test
    void testPartialSellOrderExecution() {
        Order buyOrder = new Order("1", 3, 100, OrderType.BUY, LocalDateTime.now());
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime()));
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(2, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime())));
    }

    @Test
    void testPartialBuyOrderExecution() {
        Order sellOrder = new Order("1", 3, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> remainingBuyOrders = orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime()));
        assertNotNull(remainingBuyOrders);
        assertEquals(1, remainingBuyOrders.size());
        assertEquals(2, remainingBuyOrders.get(0).getQuantity());

        assertNull(orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime())));
    }

    @Test
    void testAddSellOrderWithHigherPriceThanBuyOrder() {
        Order buyOrder = new Order("1", 5, 90, OrderType.BUY, LocalDateTime.now());
        Order sellOrder = new Order("2", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        List<Order> buyOrders = orderBook.getBuyOrders().get(new OrderKey(90, buyOrder.getCreationTime()));
        List<Order> sellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime()));

        assertNotNull(buyOrders);
        assertNotNull(sellOrders);
        assertEquals(1, buyOrders.size());
        assertEquals(1, sellOrders.size());
    }

    @Test
    void testAddBuyOrderWithLowerPriceThanSellOrder() {
        Order sellOrder = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 5, 90, OrderType.BUY, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> buyOrders = orderBook.getBuyOrders().get(new OrderKey(90, buyOrder.getCreationTime()));
        List<Order> sellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime()));

        assertNotNull(buyOrders);
        assertNotNull(sellOrders);
        assertEquals(1, buyOrders.size());
        assertEquals(1, sellOrders.size());
    }

    @Test
    void testTwoSellOrdersWithSamePriceAndOneBuyOrder() {
        Order sellOrder1 = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(1));
        Order sellOrder2 = new Order("2", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(2));
        Order buyOrder = new Order("3", 5, 100, OrderType.BUY, LocalDateTime.now());

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder2.getCreationTime()));

        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(sellOrder2, remainingSellOrders.get(0));
        assertNull(orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime())));
    }


    @Test
    void testBuyOrderExecutesSellOrderWithEarlierCreationTime() {
        Order sellOrder1 = new Order("1", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(2));
        Order sellOrder2 = new Order("2", 5, 100, OrderType.SELL, LocalDateTime.now().plusSeconds(1));
        Order buyOrder = new Order("3", 5, 100, OrderType.BUY, LocalDateTime.now());

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(buyOrder);

        // Изменяем проверку на sellOrder1, так как он должен остаться после исполнения
        List<Order> remainingSellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder1.getCreationTime()));

        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(sellOrder1, remainingSellOrders.get(0));
        assertNull(orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime())));
    }

    @Test
    void testCompareToDifferentPrices() {
        OrderKey orderKey1 = new OrderKey(100, LocalDateTime.now());
        OrderKey orderKey2 = new OrderKey(90, LocalDateTime.now());

        assertTrue(orderKey1.compareTo(orderKey2) > 0);
        assertTrue(orderKey2.compareTo(orderKey1) < 0);
    }

    @Test
    void testPartialExecutionOfOpposingOrder() {
        Order sellOrder = new Order("1", 10, 100, OrderType.SELL, LocalDateTime.now());
        Order buyOrder = new Order("2", 5, 100, OrderType.BUY, LocalDateTime.now().plusSeconds(1));

        orderBook.addOrder(sellOrder);
        orderBook.addOrder(buyOrder);

        List<Order> remainingSellOrders = orderBook.getSellOrders().get(new OrderKey(100, sellOrder.getCreationTime()));
        assertNotNull(remainingSellOrders);
        assertEquals(1, remainingSellOrders.size());
        assertEquals(5, remainingSellOrders.get(0).getQuantity());

        assertNull(orderBook.getBuyOrders().get(new OrderKey(100, buyOrder.getCreationTime())));
    }

    @Test
    void testMainMethod() {
        String[] args = {};
        Main.main(args);
    }
}
