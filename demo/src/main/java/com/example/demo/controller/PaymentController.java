package com.example.demo.controller;

import com.example.demo.model.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private static final List<Payment> paymentHistory = new ArrayList<>();

    // ========== CREATE ==========
    @PostMapping("/payment")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> payload) {
        try {
            // Validation
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

            Random rand = new Random();
            int paymentId = rand.nextInt(9000) + 1000;
            Payment payment = new Payment(paymentId, totalAmount, paymentMethod);

            boolean success = payment.processPayment();

            if (success) {
                paymentHistory.add(payment);
                System.out.println("✅ Payment processed: " + payment);
                String message = String.format(
                    "Thanh toán thành công cho đơn hàng #%d bằng %s. Tổng: %,.0f VND",
                    orderId, paymentMethod, totalAmount
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(message);
            } else {
                System.err.println("❌ Payment processing failed");
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
    public ResponseEntity<?> getAllPayments() {
        try {
            System.out.println("📖 Reading all payments, count: " + paymentHistory.size());
            return ResponseEntity.ok(paymentHistory);
        } catch (Exception e) {
            System.err.println("❌ Error reading payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc lịch sử thanh toán: " + e.getMessage());
        }
    }

    // ========== READ BY ID ==========
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable int paymentId) {
        try {
            Payment payment = paymentHistory.stream()
                    .filter(p -> p.getPaymentId() == paymentId)
                    .findFirst()
                    .orElse(null);

            if (payment != null) {
                System.out.println("📖 Found payment: " + payment);
                return ResponseEntity.ok(payment);
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
    public ResponseEntity<?> updatePayment(
            @PathVariable int paymentId,
            @RequestBody Map<String, Object> payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu cập nhật không hợp lệ");
            }

            Payment payment = paymentHistory.stream()
                    .filter(p -> p.getPaymentId() == paymentId)
                    .findFirst()
                    .orElse(null);

            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thanh toán với ID: " + paymentId);
            }

            // Update method if provided
            String newMethod = (String) payload.get("method");
            if (newMethod != null && !newMethod.trim().isEmpty()) {
                payment.setMethod(newMethod);
            }

            // Update status if provided
            String newStatus = (String) payload.get("status");
            if (newStatus != null && !newStatus.trim().isEmpty()) {
                payment.setStatus(newStatus);
            }

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
            boolean removed = paymentHistory.removeIf(p -> p.getPaymentId() == paymentId);
            if (removed) {
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