package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore 
    private Order order;

    @ManyToOne
    @JoinColumn(name = "food_id") 
    private Food food;

    private int quantity;

    public OrderDetail() {}

    public OrderDetail(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Integer getId() { return id; }
    public Order getOrder() { return order; }
    public Food getFood() { return food; }
    public int getQuantity() { return quantity; }

    public void setId(Integer id) { this.id = id; }
    public void setOrder(Order order) { this.order = order; }
    public void setFood(Food food) { this.food = food; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double subTotal() {
        if (food != null) {
            return food.getPrice() * quantity;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "food=" + (food != null ? food.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}