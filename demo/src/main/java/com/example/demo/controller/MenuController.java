package com.example.demo.controller;

import com.example.demo.model.Food;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuController {

    private static List<Food> menu = new ArrayList<>();

    // Khởi tạo dữ liệu mẫu
    static {
        menu.add(new Food(1, "Phở Bò", 50000, "Phở bò tái nạm truyền thống"));
        menu.add(new Food(2, "Phở Gà", 45000, "Phở gà xé phay"));
        menu.add(new Food(3, "Bún Bò Huế", 55000, "Bún bò cay nồng"));
        menu.add(new Food(4, "Bún Chả", 60000, "Bún chả Hà Nội"));
        menu.add(new Food(5, "Bánh Cuốn", 40000, "Bánh cuốn nóng"));
        menu.add(new Food(6, "Bánh Mì", 20000, "Bánh mì pate thịt"));
        menu.add(new Food(7, "Xôi Xéo", 25000, "Xôi xéo hành phi"));
        menu.add(new Food(8, "Miến Gà", 45000, "Miến gà thanh đạm"));
        menu.add(new Food(9, "Cháo Lòng", 35000, "Cháo lòng nóng hổi"));
        menu.add(new Food(10, "Hủ Tiếu Nam Vang", 50000, "Hủ tiếu Nam Vang"));
    }

    // ========== CREATE ==========
    @PostMapping("/menu")
    public ResponseEntity<?> createFood(@RequestBody Food food) {
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

            // Check duplicate ID
            boolean exists = menu.stream().anyMatch(f -> f.getFoodID() == food.getFoodID());
            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Món ăn với ID " + food.getFoodID() + " đã tồn tại");
            }

            menu.add(food);
            System.out.println("✅ Created food: " + food);
            return ResponseEntity.status(HttpStatus.CREATED).body(food);

        } catch (Exception e) {
            System.err.println("❌ Error creating food: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo món ăn: " + e.getMessage());
        }
    }

    // ========== READ ALL ==========
    @GetMapping("/menu")
    public ResponseEntity<?> getAllFoods() {
        try {
            System.out.println("📖 Reading all foods, count: " + menu.size());
            return ResponseEntity.ok(menu);
        } catch (Exception e) {
            System.err.println("❌ Error reading menu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc menu: " + e.getMessage());
        }
    }

    // ========== READ BY ID ==========
    @GetMapping("/menu/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable int id) {
        try {
            Food food = menu.stream()
                    .filter(f -> f.getFoodID() == id)
                    .findFirst()
                    .orElse(null);

            if (food != null) {
                System.out.println("📖 Found food: " + food);
                return ResponseEntity.ok(food);
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

            for (int i = 0; i < menu.size(); i++) {
                if (menu.get(i).getFoodID() == id) {
                    updatedFood.setFoodID(id);
                    menu.set(i, updatedFood);
                    System.out.println("✏️ Updated food: " + updatedFood);
                    return ResponseEntity.ok(updatedFood);
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy món ăn với ID: " + id);

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
            boolean removed = menu.removeIf(f -> f.getFoodID() == id);
            if (removed) {
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

    // Helper method cho OrderController
    public Food getFoodByIdHelper(int id) {
        return menu.stream()
                .filter(f -> f.getFoodID() == id)
                .findFirst()
                .orElse(null);
    }
}