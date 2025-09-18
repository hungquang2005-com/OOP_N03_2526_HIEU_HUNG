import java.util.ArrayList;
import java.util.List;

public class RestaurantManagement {
    private List<Food> foods;
    private List<Order> orders;

    public RestaurantManagement() {
        this.foods = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public void addFood(Food food) {
        foods.add(food);
        System.out.println("Added food: " + food.getName());
    }

    public void createOrder(Order order) {
        orders.add(order);
        System.out.println("Created order ID: " + order.getOrderID());
    }

    public void generateReport() {
        System.out.println("=== Restaurant Report ===");
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getOrderID() +
                    ", Total: " + order.getTotal() + 
                    ", Status: " + order.getStatus());
        }
    }

    public List<Food> getFoods() {
        return foods;
    }

    public List<Order> getOrders() {
        return orders;
    }
}

