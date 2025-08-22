import java.util.ArrayList;
import java.util.List;

public class FoodService {
    private List<Food> foods;

    public FoodService() {
        foods = new ArrayList<>();
    }

    public void addFood(Food food) {
        foods.add(food);
    }

    public void showFoods() {
        System.out.println("Danh sách món ăn:");
        for (Food food : foods) {
            System.out.println(food);
        }
    }
}

