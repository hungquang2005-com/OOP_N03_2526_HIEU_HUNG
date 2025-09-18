import java.util.*;

public class Order {
    private int orderID;
    private Date orderDate;
    private String status;
    private List<OrderDetail> orderDetails;
    private double total; // lưu tổng tiền sau giảm giá

    public Order(int orderID, Date orderDate, String status) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = new ArrayList<>();
        this.total = 0;
    }

    public int getOrderID() {
        return orderID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
    }

    public double calculateTotal() {
        double sum = 0;
        for (OrderDetail detail : orderDetails) {
            sum += detail.subTotal();
        }
        this.total = sum; // cập nhật total mặc định
        return sum;
    }

    // ===== Getter/Setter cho total =====
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
