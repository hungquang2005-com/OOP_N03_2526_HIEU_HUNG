package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TimeTest {

    @Test
    void testLayThoiGianHienTai() {
        String currentTime = Time.layThoiGianHienTai();
        assertNotNull(currentTime);
        assertTrue(currentTime.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void testDinhDang() {
        Date testDate = new Date();
        String formatted = Time.dinhDang(testDate);
        assertNotNull(formatted);
        assertTrue(formatted.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void testDinhDangWithNull() {
        String result = Time.dinhDang(null);
        assertEquals("", result);
    }

    @Test
    void testTinhKhoangCachPhut() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 5 * 60 * 1000); // +5 minutes
        
        long minutes = Time.tinhKhoangCachPhut(start, end);
        assertEquals(5, minutes);
    }

    @Test
    void testTinhKhoangCachPhutWithNull() {
        assertEquals(0, Time.tinhKhoangCachPhut(null, new Date()));
        assertEquals(0, Time.tinhKhoangCachPhut(new Date(), null));
        assertEquals(0, Time.tinhKhoangCachPhut(null, null));
    }

    @Test
    void testTinhKhoangCachGio() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3 * 60 * 60 * 1000); // +3 hours
        
        long hours = Time.tinhKhoangCachGio(start, end);
        assertEquals(3, hours);
    }

    @Test
    void testTinhKhoangCachGioWithNull() {
        assertEquals(0, Time.tinhKhoangCachGio(null, new Date()));
        assertEquals(0, Time.tinhKhoangCachGio(new Date(), null));
        assertEquals(0, Time.tinhKhoangCachGio(null, null));
    }
}
