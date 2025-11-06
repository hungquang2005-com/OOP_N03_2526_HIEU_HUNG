package com.example.demo.controller;

import com.example.demo.model.Food;
import com.example.demo.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MenuController {

    @Autowired
    private FoodRepository foodRepository;

    // ========== CREATE ==========
    @PostMapping("/menu")
    public ResponseEntity<?> addFood(@RequestBody Food food) {
        try {
            // Validation
            if (food == null) {
                return ResponseEntity.badRequest().body("Dữ liệu món ăn không hợp lệ");
            }
            if (food.getName() == null || food.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên món ăn không được để trống");
            }
            if (food.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Giá món ăn phải lớn hơn 0");
            }

            // ✅ Tự động set ảnh mặc định nếu không có
            if (food.getImage() == null || food.getImage().trim().isEmpty()) {
                food.setImage("https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400");
            }

            Food savedFood = foodRepository.save(food);
            System.out.println("✅ Added new food: " + savedFood.getName() + " (Image: " + savedFood.getImage() + ")");
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFood);
        } catch (Exception e) {
            System.err.println("❌ Error adding food: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm món ăn: " + e.getMessage());
        }
    }

    // ========== READ ALL ==========
    @GetMapping("/menu")
    public ResponseEntity<List<Food>> getMenu() {
        try {
            List<Food> menu = foodRepository.findAll();
            System.out.println("📖 Reading all foods, count: " + menu.size());
            return ResponseEntity.ok(menu);
        } catch (Exception e) {
            System.err.println("❌ Error reading menu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== READ BY ID ==========
    @GetMapping("/menu/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable int id) {
        try {
            Optional<Food> foodOpt = foodRepository.findById(id);
            if (foodOpt.isPresent()) {
                System.out.println("📖 Found food: " + foodOpt.get());
                return ResponseEntity.ok(foodOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy món ăn với ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading food: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc món ăn: " + e.getMessage());
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/menu/{id}")
    public ResponseEntity<?> updateFood(@PathVariable int id, @RequestBody Food updatedFood) {
        try {
            // Validation
            if (updatedFood == null) {
                return ResponseEntity.badRequest().body("Dữ liệu món ăn không hợp lệ");
            }
            if (updatedFood.getName() == null || updatedFood.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên món ăn không được để trống");
            }
            if (updatedFood.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Giá món ăn phải lớn hơn 0");
            }

            Optional<Food> existingFoodOpt = foodRepository.findById(id);

            if (existingFoodOpt.isPresent()) {
                Food existingFood = existingFoodOpt.get();
                existingFood.setName(updatedFood.getName());
                existingFood.setPrice(updatedFood.getPrice());
                existingFood.setDescription(updatedFood.getDescription());
                
                // ✅ Cập nhật image
                if (updatedFood.getImage() != null && !updatedFood.getImage().trim().isEmpty()) {
                    existingFood.setImage(updatedFood.getImage());
                } else {
                    // Nếu không có image mới, giữ nguyên image cũ
                    // Hoặc có thể set ảnh mặc định
                }

                Food savedFood = foodRepository.save(existingFood);
                System.out.println("✏️ Updated food: " + savedFood + " (Image: " + savedFood.getImage() + ")");
                return ResponseEntity.ok(savedFood);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy món ăn với ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating food: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật món ăn: " + e.getMessage());
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable int id) {
        try {
            if (foodRepository.existsById(id)) {
                foodRepository.deleteById(id);
                System.out.println("🗑️ Deleted food ID: " + id);
                return ResponseEntity.ok("Đã xóa món ăn ID: " + id);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy món ăn với ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting food: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa món ăn: " + e.getMessage());
        }
    }
}