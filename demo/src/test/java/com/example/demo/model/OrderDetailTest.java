
package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderDetailTest {

    private OrderDetail orderDetail;
    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food(1, "Phở Bò", 50000, "Phở bò truyền thống");
        orderDetail = new OrderDetail(food, 2);
    }

    @Test
    void testOrderDetailCreation() {
        assertNotNull(orderDetail);
        assertEquals(food, orderDetail.getFood());
        assertEquals(2, orderDetail.getQuantity());
    }

    @Test
    void testSubTotal() {
        double subTotal = orderDetail.subTotal();
        assertEquals(100000, subTotal); // 50000 * 2
    }

    @Test
    void testSubTotalWithNullFood() {
        OrderDetail emptyDetail = new OrderDetail();
        assertEquals(0, emptyDetail.subTotal());
    }

    @Test
    void testSetters() {
        Order order = new Order();
        Food newFood = new Food(2, "Bún Chả", 40000, "Bún chả");
        
        orderDetail.setId(1);
        orderDetail.setOrder(order);
        orderDetail.setFood(newFood);
        orderDetail.setQuantity(3);

        assertEquals(1, orderDetail.getId());
        assertEquals(order, orderDetail.getOrder());
        assertEquals(newFood, orderDetail.getFood());
        assertEquals(3, orderDetail.getQuantity());
    }

    @Test
    void testToString() {
        String result = orderDetail.toString();
        assertTrue(result.contains("food=Phở Bò"));
        assertTrue(result.contains("quantity=2"));
    }

    @Test
    void testToStringWithNullFood() {
        OrderDetail emptyDetail = new OrderDetail();
        String result = emptyDetail.toString();
        assertTrue(result.contains("food=null"));
    }
}
