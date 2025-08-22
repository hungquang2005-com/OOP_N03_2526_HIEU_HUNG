import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Food> items;

    public Order() {
        items = new ArrayList<>();
    }

    public void addFood(Food food) {
        items.add(food);
    }

    public double getTotal() {
        double total = 0;
        for (Food f : items) {
            total += f.getPrice();
        }
        return total;
    }

    public void showOrder() {
        System.out.println("Đơn hàng:");
        for (Food f : items) {
            System.out.println(f);
        }
        System.out.println("Tổng tiền: " + getTotal() + " VND");
    }
}

