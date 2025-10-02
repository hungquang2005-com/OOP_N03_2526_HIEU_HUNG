package practice;
import java.util.*;

public class Order {
    private int orderID;
    private Date orderDate;
    private String status;
    private List<OrderDetail> orderDetails;
    private double total; // lưu tổng tiền sau giảm giá

    // Lưu trữ CRUD
    private static List<Order> orders = new ArrayList<>();

    public Order(int orderID, Date orderDate, String status) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = new ArrayList<>();
        this.total = 0;
    }

    public int getOrderID() { return orderID; }
    public Date getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public List<OrderDetail> getOrderDetails() { return orderDetails; }
    public double getTotal() { return total; }

    public void setTotal(double total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }

    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
    }

    public double calculateTotal() {
        double sum = 0;
        for (OrderDetail detail : orderDetails) {
            sum += detail.subTotal();
        }
        this.total = sum;
        return sum;
    }

    // ========== CRUD ==========
    public static void create(Order o) {
        orders.add(o);
    }

    public static List<Order> readAll() {
        return orders;
    }

    public static Order readById(int id) {
        for (Order o : orders) {
            if (o.getOrderID() == id) return o;
        }
        return null;
    }

    public static void update(int id, String newStatus) {
        Order o = readById(id);
        if (o != null) {
            o.setStatus(newStatus);
        }
    }

    public static void delete(int id) {
        orders.removeIf(o -> o.getOrderID() == id);
    }
}
