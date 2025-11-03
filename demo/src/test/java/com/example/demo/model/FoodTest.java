package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FoodTest {

    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food(1, "Phở Bò", 50000, "Phở bò truyền thống");
    }

    @Test
    void testFoodCreation() {
        assertNotNull(food);
        assertEquals(1, food.getFoodID());
        assertEquals("Phở Bò", food.getName());
        assertEquals(50000, food.getPrice());
        assertEquals("Phở bò truyền thống", food.getDescription());
    }

    @Test
    void testSetters() {
        food.setFoodID(2);
        food.setName("Bún Chả");
        food.setPrice(40000);
        food.setDescription("Bún chả Hà Nội");

        assertEquals(2, food.getFoodID());
        assertEquals("Bún Chả", food.getName());
        assertEquals(40000, food.getPrice());
        assertEquals("Bún chả Hà Nội", food.getDescription());
    }

    @Test
    void testToString() {
        String result = food.toString();
        assertTrue(result.contains("foodID=1"));
        assertTrue(result.contains("name='Phở Bò'"));
        assertTrue(result.contains("price=50000"));
    }

    @Test
    void testDefaultConstructor() {
        Food emptyFood = new Food();
        assertNotNull(emptyFood);
    }
}