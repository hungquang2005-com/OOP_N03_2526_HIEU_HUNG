import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int orderID;
    private Date orderDate;
    private String status;
    private List<OrderDetail> orderDetails;

    public Order(int orderID, Date orderDate, String status) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = new ArrayList<>();
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
        double total = 0;
        for (OrderDetail detail : orderDetails) {
            total += detail.subTotal();
        }
        return total;
    }
}


