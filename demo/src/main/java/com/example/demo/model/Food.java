package com.example.demo.model;

import jakarta.persistence.*;

@Entity                
@Table(name = "foods") 
public class Food {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int foodID;

    private String name;
    private double price;
    private String description;
    
    // ✅ THÊM TRƯỜNG IMAGE
    @Column(length = 500)
    private String image;

    public Food() {}

    public Food(int foodID, String name, double price, String description) {
        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Constructor có image
    public Food(int foodID, String name, double price, String description, String image) {
        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
    }

    // Getters
    public int getFoodID() { return foodID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImage() { return image; }  // ✅ THÊM GETTER

    // Setters
    public void setFoodID(int foodID) { this.foodID = foodID; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImage(String image) { this.image = image; }  // ✅ THÊM SETTER

    @Override
    public String toString() {
        return "Food{" +
                "foodID=" + foodID +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                '}';
    }
}