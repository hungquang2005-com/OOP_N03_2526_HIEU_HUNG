package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiningTableTest {

    private DiningTable table;

    @BeforeEach
    void setUp() {
        table = new DiningTable(1, 4);
    }

    @Test
    void testTableCreation() {
        assertNotNull(table);
        assertEquals(1, table.getTableId());
        assertEquals(4, table.getCapacity());
        assertFalse(table.isReserved());
    }

    @Test
    void testReserve() {
        assertFalse(table.isReserved());
        table.reserve();
        assertTrue(table.isReserved());
    }

    @Test
    void testReserveAlreadyReserved() {
        table.reserve();
        assertTrue(table.isReserved());
        table.reserve(); // Try to reserve again
        assertTrue(table.isReserved()); // Should still be reserved
    }

    @Test
    void testRelease() {
        table.reserve();
        assertTrue(table.isReserved());
        table.release();
        assertFalse(table.isReserved());
    }

    @Test
    void testReleaseNotReserved() {
        assertFalse(table.isReserved());
        table.release(); // Try to release when not reserved
        assertFalse(table.isReserved()); // Should still be not reserved
    }

    @Test
    void testSetters() {
        table.setTableId(2);
        table.setCapacity(6);
        table.setReserved(true);

        assertEquals(2, table.getTableId());
        assertEquals(6, table.getCapacity());
        assertTrue(table.isReserved());
    }

    @Test
    void testToString() {
        String result = table.toString();
        assertTrue(result.contains("tableId=1"));
        assertTrue(result.contains("capacity=4"));
        assertTrue(result.contains("reserved=false"));
    }
}