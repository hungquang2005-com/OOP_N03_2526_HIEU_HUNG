package com.example.demo.controller;

import com.example.demo.model.Food;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.repository.FoodRepository; // <-- IMPORT MỚI
import com.example.demo.repository.OrderRepository; // <-- IMPORT MỚI
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository; 

    @Autowired
    private FoodRepository foodRepository; 
   
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("items")) {
                return ResponseEntity.badRequest().body("Thiếu danh sách món ăn");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Integer>> items = (List<Map<String, Integer>>) payload.get("items");
            if (items == null || items.isEmpty()) {
                return ResponseEntity.badRequest().body("Đơn hàng phải có ít nhất 1 món");
            }


            Order newOrder = new Order();
            newOrder.setStatus("Đang xử lý");
            
            for (Map<String, Integer> item : items) {
                Integer foodId = item.get("foodId");
                Integer quantity = item.get("quantity");

                if (foodId == null || quantity == null || quantity <= 0) {
                    return ResponseEntity.badRequest().body("Thông tin món ăn không hợp lệ");
                }
                
                Optional<Food> foodOpt = foodRepository.findById(foodId);
                if (!foodOpt.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy món ăn với ID: " + foodId);
                }

                Food food = foodOpt.get();
                OrderDetail detail = new OrderDetail(food, quantity);
                newOrder.addOrderDetail(detail); 
            }

            newOrder.calculateTotal();
            
            Order savedOrder = orderRepository.save(newOrder);

            System.out.println("✅ Created new order: " + savedOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);

        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(orderOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy đơn hàng với ID: " + orderId);
        }
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int orderId, @RequestBody Map<String, String> payload) {
        try {
   
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy đơn hàng với ID: " + orderId);
            }

            String newStatus = payload.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Trạng thái không được để trống");
            }

            Order order = orderOpt.get();
            order.setStatus(newStatus);
            orderRepository.save(order); 

            System.out.println("✏️ Updated order status: " + order);
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            System.err.println("❌ Error updating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật đơn hàng: " + e.getMessage());
        }
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {
        try {
      
            if (orderRepository.existsById(orderId)) {
                orderRepository.deleteById(orderId); 
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