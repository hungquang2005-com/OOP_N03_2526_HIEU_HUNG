package controller;

import model.Food;
import java.util.*;

public class MenuController {
    private List<Food> menu;

    public MenuController() {
        // Khởi tạo menu mặc định
        menu = Arrays.asList(
            new Food(1, "Phở Bò", 50000, "Phở bò tái nạm truyền thống"),
            new Food(2, "Phở Gà", 45000, "Phở gà xé phay"),
            new Food(3, "Bún Bò Huế", 55000, "Bún bò cay nồng"),
            new Food(4, "Bún Chả", 60000, "Bún chả Hà Nội"),
            new Food(5, "Bánh Cuốn", 40000, "Bánh cuốn nóng"),
            new Food(6, "Bánh Mì", 20000, "Bánh mì pate thịt"),
            new Food(7, "Xôi Xéo", 25000, "Xôi xéo hành phi"),
            new Food(8, "Miến Gà", 45000, "Miến gà thanh đạm"),
            new Food(9, "Cháo Lòng", 35000, "Cháo lòng nóng hổi"),
            new Food(10,"Hủ Tiếu Nam Vang", 50000, "Hủ tiếu Nam Vang")
        );
    }

    public List<Food> getMenu() {
        return menu;
    }

    public void displayMenu() {
        System.out.println("\n=== MENU ===");
        for (Food f : menu) {
            System.out.println(f.getFoodID() + ". " + f.getName() + " - " + f.getPrice() + " VND");
        }
    }
}
