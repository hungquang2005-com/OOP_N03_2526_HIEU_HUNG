package practice;
import java.util.ArrayList;
import java.util.List;

public class Category {
    private int categoryID;
    private String categoryName;
    private List<Food> foods;

    public Category(int categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.foods = new ArrayList<>();
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void addFood(Food food) {
        foods.add(food);
    }

    public void printFoodsRecursive(int index) {
        if (index >= foods.size()) {
            return;
        }
        System.out.println("- " + foods.get(index).getName());
        printFoodsRecursive(index + 1);
    }

    public Food findFoodByNameRecursive(String name, int index) {
        if (index >= foods.size()) {
            return null; 
        }
        if (foods.get(index).getName().equalsIgnoreCase(name)) {
            return foods.get(index); 
        }
        return findFoodByNameRecursive(name, index + 1);
    }
}