package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private Food food1;
    private Food food2;

    @BeforeEach
    void setUp() {
        order = new Order();
        food1 = new Food(1, "Phở Bò", 50000, "Phở bò truyền thống");
        food2 = new Food(2, "Bún Chả", 40000, "Bún chả Hà Nội");
    }

    @Test
    void testOrderCreation() {
        assertNotNull(order);
        assertNotNull(order.getOrderDate());
        assertNotNull(order.getOrderDetails());
        assertTrue(order.getOrderDetails().isEmpty());
    }

    @Test
    void testAddOrderDetail() {
        OrderDetail detail = new OrderDetail(food1, 2);
        order.addOrderDetail(detail);

        assertEquals(1, order.getOrderDetails().size());
        assertEquals(order, detail.getOrder());
    }

    @Test
    void testCalculateTotal() {
        OrderDetail detail1 = new OrderDetail(food1, 2); // 50000 * 2 = 100000
        OrderDetail detail2 = new OrderDetail(food2, 3); // 40000 * 3 = 120000
        
        order.addOrderDetail(detail1);
        order.addOrderDetail(detail2);
        
        double total = order.calculateTotal();
        
        assertEquals(220000, total);
        assertEquals(220000, order.getTotal());
    }

    @Test
    void testCalculateTotalEmpty() {
        double total = order.calculateTotal();
        assertEquals(0, total);
    }

    @Test
    void testSetters() {
        Date testDate = new Date();
        order.setOrderId(1);
        order.setOrderDate(testDate);
        order.setStatus("Hoàn thành");
        order.setTotal(100000);

        assertEquals(1, order.getOrderId());
        assertEquals(testDate, order.getOrderDate());
        assertEquals("Hoàn thành", order.getStatus());
        assertEquals(100000, order.getTotal());
    }

    @Test
    void testParameterizedConstructor() {
        Date testDate = new Date();
        Order customOrder = new Order(1, testDate, "Đang xử lý");

        assertEquals(1, customOrder.getOrderId());
        assertEquals(testDate, customOrder.getOrderDate());
        assertEquals("Đang xử lý", customOrder.getStatus());
        assertEquals(0, customOrder.getTotal());
    }
}
