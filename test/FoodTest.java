public class FoodTest {
    public static void main(String[] args) {
        FoodService service = new FoodService();

        service.addFood(new Food("Phở Bò", 45000));
        service.addFood(new Food("Cơm Gà", 50000));

        service.showFoods();
    }
}


