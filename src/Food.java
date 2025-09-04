public class Food {
    private int foodID;
    private String name;
    private double price;
    private String description;

    public Food(int foodID, String name, double price, String description) {
        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getFoodID() {
        return foodID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}




