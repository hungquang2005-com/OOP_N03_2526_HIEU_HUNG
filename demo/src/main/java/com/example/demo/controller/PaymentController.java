package com.example.demo.controller;

import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    // ========== CREATE ==========
    @PostMapping("/payment")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu thanh toán không hợp lệ");
            }

            Integer orderId = (Integer) payload.get("orderId");
            String paymentMethod = (String) payload.get("paymentMethod");
            Number totalAmountNumber = (Number) payload.get("totalAmount");

            if (orderId == null || totalAmountNumber == null || paymentMethod == null) {
                return ResponseEntity.badRequest().body("Thông tin thanh toán không đầy đủ");
            }

            double totalAmount = totalAmountNumber.doubleValue();
            if (totalAmount <= 0) {
                return ResponseEntity.badRequest().body("Số tiền thanh toán phải lớn hơn 0");
            }

            List<String> validMethods = Arrays.asList("Tiền mặt", "QR Code", "Thẻ tín dụng");
            if (!validMethods.contains(paymentMethod)) {
                return ResponseEntity.badRequest()
                        .body("Phương thức thanh toán không hợp lệ. Chỉ chấp nhận: Tiền mặt, QR Code, Thẻ tín dụng");
            }

            Payment newPayment = new Payment();
            newPayment.setAmount(totalAmount);
            newPayment.setMethod(paymentMethod);

            boolean success = newPayment.processPayment();

            if (success) {
                Payment savedPayment = paymentRepository.save(newPayment);
                System.out.println("✅ Payment processed and saved: " + savedPayment);
                
                String message = String.format(
                    "Thanh toán thành công cho đơn hàng #%d bằng %s. Tổng: %,.0f VND",
                    orderId, paymentMethod, totalAmount
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(message);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Xử lý thanh toán thất bại");
            }

        } catch (ClassCastException e) {
            System.err.println("❌ Invalid data format: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Dữ liệu gửi lên không đúng định dạng: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error processing payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã có lỗi xảy ra: " + e.getMessage());
        }
    }

    // ========== READ ALL ==========
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            System.out.println("📖 Reading all payments, count: " + payments.size());
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            System.err.println("❌ Error reading payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== READ BY ID ========== (THÊM MỚI)
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable int paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isPresent()) {
                System.out.println("📖 Found payment: " + paymentOpt.get());
                return ResponseEntity.ok(paymentOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thanh toán với ID: " + paymentId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc thanh toán: " + e.getMessage());
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/payments/{paymentId}")
    public ResponseEntity<?> updatePayment(@PathVariable int paymentId, 
                                          @RequestBody Map<String, Object> payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu cập nhật không hợp lệ");
            }

            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thanh toán với ID: " + paymentId);
            }

            Payment payment = paymentOpt.get();

            String newMethod = (String) payload.get("method");
            if (newMethod != null && !newMethod.trim().isEmpty()) {
                payment.setMethod(newMethod);
            }

            String newStatus = (String) payload.get("status");
            if (newStatus != null && !newStatus.trim().isEmpty()) {
                payment.setStatus(newStatus);
            }

            paymentRepository.save(payment);
            System.out.println("✏️ Updated payment: " + payment);
            return ResponseEntity.ok(payment);

        } catch (Exception e) {
            System.err.println("❌ Error updating payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật thanh toán: " + e.getMessage());
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable int paymentId) {
        try {
            if (paymentRepository.existsById(paymentId)) {
                paymentRepository.deleteById(paymentId);
                System.out.println("🗑️ Deleted payment ID: " + paymentId);
                return ResponseEntity.ok("Đã xóa thanh toán ID: " + paymentId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thanh toán với ID: " + paymentId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa thanh toán: " + e.getMessage());
        }
    }
}