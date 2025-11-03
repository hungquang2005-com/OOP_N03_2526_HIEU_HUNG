package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment(1, 100000, "Tiền mặt");
    }

    @Test
    void testPaymentCreation() {
        assertNotNull(payment);
        assertEquals(1, payment.getPaymentId());
        assertEquals(100000, payment.getAmount());
        assertEquals("Tiền mặt", payment.getMethod());
        assertEquals("Đang xử lý", payment.getStatus());
        assertNotNull(payment.getTimestamp());
        assertNotNull(payment.getPaymentDate());
    }

    @Test
    void testProcessPayment() {
        boolean result = payment.processPayment();
        
        assertTrue(result);
        assertEquals("Thành công", payment.getStatus());
    }

    @Test
    void testSetters() {
        payment.setPaymentId(2);
        payment.setAmount(200000);
        payment.setMethod("QR Code");
        payment.setStatus("Hoàn thành");

        assertEquals(2, payment.getPaymentId());
        assertEquals(200000, payment.getAmount());
        assertEquals("QR Code", payment.getMethod());
        assertEquals("Hoàn thành", payment.getStatus());
    }

    @Test
    void testDefaultConstructor() {
        Payment newPayment = new Payment();
        assertNotNull(newPayment);
        assertEquals("Đang xử lý", newPayment.getStatus());
        assertNotNull(newPayment.getTimestamp());
    }

    @Test
    void testToString() {
        String result = payment.toString();
        assertTrue(result.contains("paymentId=1"));
        assertTrue(result.contains("amount=100000"));
        assertTrue(result.contains("method='Tiền mặt'"));
    }
}

