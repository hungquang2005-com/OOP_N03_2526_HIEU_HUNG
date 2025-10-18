package com.example.demo.model;

public class OrderDetail {
    private Food food;
    private int quantity;

    public OrderDetail() {}

    public OrderDetail(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() { return food; }
    public int getQuantity() { return quantity; }

    public void setFood(Food food) { this.food = food; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double subTotal() {
        return food.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "food=" + food.getName() +
                ", quantity=" + quantity +
                ", subtotal=" + subTotal() +
                '}';
    }
}