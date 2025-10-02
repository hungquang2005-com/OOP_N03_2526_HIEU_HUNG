package model;
import java.util.ArrayList;
import java.util.List;

public class Food {
    private int foodID;
    private String name;
    private double price;
    private String description;

    // Lưu trữ CRUD
    private static List<Food> foods = new ArrayList<>();

    public Food(int foodID, String name, double price, String description) {
        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getFoodID() { return foodID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }

    // ========== CRUD ==========
    public static void create(Food food) {
        foods.add(food);
    }

    public static List<Food> readAll() {
        return foods;
    }

    public static Food readById(int id) {
        for (Food f : foods) {
            if (f.getFoodID() == id) return f;
        }
        return null;
    }

    public static void update(int id, String newName, double newPrice, String newDesc) {
        Food f = readById(id);
        if (f != null) {
            f.setName(newName);
            f.setPrice(newPrice);
            f.setDescription(newDesc);
        }
    }

    public static void delete(int id) {
        foods.removeIf(f -> f.getFoodID() == id);
    }
}
