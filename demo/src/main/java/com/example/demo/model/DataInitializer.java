package com.example.demo.model;

import com.example.demo.model.Food;
import com.example.demo.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu database trống thì thêm dữ liệu mẫu
        if (foodRepository.count() == 0) {
            System.out.println("🍜 Khởi tạo menu với dữ liệu mẫu...");
            
            foodRepository.save(new Food(0, "Phở Bò", 50000, "Phở bò tái nạm truyền thống"));
            foodRepository.save(new Food(0, "Phở Gà", 45000, "Phở gà xé phay"));
            foodRepository.save(new Food(0, "Bún Bò Huế", 55000, "Bún bò cay nồng"));
            foodRepository.save(new Food(0, "Bún Chả", 60000, "Bún chả Hà Nội"));
            foodRepository.save(new Food(0, "Bánh Cuốn", 40000, "Bánh cuốn nóng"));
            foodRepository.save(new Food(0, "Bánh Mì", 20000, "Bánh mì pate thịt"));
            foodRepository.save(new Food(0, "Xôi Xéo", 25000, "Xôi xéo hành phi"));
            foodRepository.save(new Food(0, "Miến Gà", 45000, "Miến gà thanh đạm"));
            foodRepository.save(new Food(0, "Cháo Lòng", 35000, "Cháo lòng nóng hổi"));
            foodRepository.save(new Food(0, "Hủ Tiếu Nam Vang", 50000, "Hủ tiếu Nam Vang"));
            foodRepository.save(new Food(0, "Cơm Tấm", 45000, "Cơm tấm sườn bì chả"));
            
            System.out.println("✅ Đã khởi tạo menu với 11 món ăn!");
        } else {
            System.out.println("ℹ️ Menu đã có " + foodRepository.count() + " món ăn.");
        }
    }
}