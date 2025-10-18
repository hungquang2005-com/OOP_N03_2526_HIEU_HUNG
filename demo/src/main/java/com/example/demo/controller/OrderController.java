
package com.example.demo.controller;

import com.example.demo.model.Food;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private MenuController menuController;

    private static final Set<Integer> usedOrderIds = new HashSet<>();
    private static final List<Order> orderHistory = new ArrayList<>();

    // ========== CREATE ==========
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> payload) {
        try {
            // Validation
            if (payload == null || !payload.containsKey("items")) {
                return ResponseEntity.badRequest().body("Thiếu danh sách món ăn");
            }

            List<Map<String, Integer>> items = (List<Map<String, Integer>>) payload.get("items");

            if (items == null || items.isEmpty()) {
                return ResponseEntity.badRequest().body("Đơn hàng phải có ít nhất 1 món");
            }

            // Generate unique order ID
            int orderId;
            Random rand = new Random();
            do {
                orderId = rand.nextInt(9000) + 1000;
            } while (usedOrderIds.contains(orderId));
            usedOrderIds.add(orderId);

            Order order = new Order(orderId, new Date(), "Chưa thanh toán");

            // Add order details
            for (Map<String, Integer> item : items) {
                Integer foodId = item.get("foodId");
                Integer quantity = item.get("quantity");

                if (foodId == null || quantity == null || quantity <= 0) {
                    return ResponseEntity.badRequest()
                            .body("Thông tin món ăn không hợp lệ");
                }

                Food food = menuController.getFoodByIdHelper(foodId);

                if (food == null) {
                    return ResponseEntity.badRequest()
                            .body("Không tìm thấy món ăn với ID: " + foodId);
                }

                order.addOrderDetail(new OrderDetail(food, quantity));
            }

            // Calculate total
            order.calculateTotal();

            // Apply discount
            String discountCode = (String) payload.get("discountCode");
            if (discountCode != null && discountCode.equalsIgnoreCase("DISCOUNT10")) {
                double newTotal = order.getTotal() * 0.9;
                order.setTotal(newTotal);
            }

            orderHistory.add(order);
            System.out.println("✅ Created order: " + order);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);

        } catch (ClassCastException e) {
            System.err.println("❌ Invalid data format: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Định dạng dữ liệu không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    // ========== READ ALL ==========
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        try {
            System.out.println("📖 Reading all orders, count: " + orderHistory.size());
            return ResponseEntity.ok(orderHistory);
        } catch (Exception e) {
            System.err.println("❌ Error reading orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc đơn hàng: " + e.getMessage());
        }
    }

    // ========== READ BY ID ==========
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        try {
            Order order = orderHistory.stream()
                    .filter(o -> o.getOrderId() == orderId)
                    .findFirst()
                    .orElse(null);

            if (order != null) {
                System.out.println("📖 Found order: " + order);
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy đơn hàng với ID: " + orderId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc đơn hàng: " + e.getMessage());
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int orderId,
            @RequestBody Map<String, String> payload) {
        try {
            if (payload == null || !payload.containsKey("status")) {
                return ResponseEntity.badRequest().body("Thiếu thông tin trạng thái");
            }

            String newStatus = payload.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Trạng thái không được để trống");
            }

            Order order = orderHistory.stream()
                    .filter(o -> o.getOrderId() == orderId)
                    .findFirst()
                    .orElse(null);

            if (order != null) {
                order.setStatus(newStatus);
                System.out.println("✏️ Updated order status: " + order);
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy đơn hàng với ID: " + orderId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật đơn hàng: " + e.getMessage());
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {
        try {
            boolean removed = orderHistory.removeIf(o -> o.getOrderId() == orderId);
            if (removed) {
                usedOrderIds.remove(orderId);
                System.out.println("🗑️ Deleted order ID: " + orderId);
                return ResponseEntity.ok("Đã xóa đơn hàng ID: " + orderId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy đơn hàng với ID: " + orderId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa đơn hàng: " + e.getMessage());
        }
    }
}