package com.example.demo.controller;

import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentController paymentController;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
        
        testPayment = new Payment();
        testPayment.setPaymentId(1);
        testPayment.setAmount(100000);
        testPayment.setMethod("Tiền mặt");
        testPayment.setStatus("Thành công");
    }

    @Test
    void processPayment_Success_Cash() throws Exception {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1);
        paymentRequest.put("totalAmount", 100000);
        paymentRequest.put("paymentMethod", "Tiền mặt");

        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_Success_QRCode() throws Exception {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1);
        paymentRequest.put("totalAmount", 100000);
        paymentRequest.put("paymentMethod", "QR Code");

        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_Success_CreditCard() throws Exception {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1);
        paymentRequest.put("totalAmount", 100000);
        paymentRequest.put("paymentMethod", "Thẻ tín dụng");

        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_InvalidMethod() throws Exception {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1);
        paymentRequest.put("totalAmount", 100000);
        paymentRequest.put("paymentMethod", "Bitcoin");

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_NegativeAmount() throws Exception {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1);
        paymentRequest.put("totalAmount", -1000);
        paymentRequest.put("paymentMethod", "Tiền mặt");

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_MissingData() throws Exception {
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPayments_Success() throws Exception {
        Payment payment2 = new Payment();
        payment2.setPaymentId(2);
        payment2.setAmount(200000);
        payment2.setMethod("QR Code");

        List<Payment> payments = Arrays.asList(testPayment, payment2);
        when(paymentRepository.findAll()).thenReturn(payments);

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllPayments_Empty() throws Exception {
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getPaymentById_Success() throws Exception {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(testPayment));

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentById_NotFound() throws Exception {
        when(paymentRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePayment_Success() throws Exception {
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("method", "QR Code");
        updateRequest.put("status", "Hoàn thành");

        when(paymentRepository.findById(1)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        mockMvc.perform(put("/api/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void updatePayment_NotFound() throws Exception {
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("method", "QR Code");

        when(paymentRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/payments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePayment_PartialUpdate() throws Exception {
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("status", "Thất bại");

        when(paymentRepository.findById(1)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        mockMvc.perform(put("/api/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePayment_Success() throws Exception {
        when(paymentRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/payments/1"))
                .andExpect(status().isOk());

        verify(paymentRepository, times(1)).deleteById(1);
    }

    @Test
    void deletePayment_NotFound() throws Exception {
        when(paymentRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(delete("/api/payments/999"))
                .andExpect(status().isNotFound());

        verify(paymentRepository, never()).deleteById(anyInt());
    }
}