package model;
public class OrderDetail {
    private Food food;
    private int quantity;

    public OrderDetail(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public int getQuantity() {
        return quantity;
    }

    public double subTotal() {
        return food.getPrice() * quantity;
    }
}
