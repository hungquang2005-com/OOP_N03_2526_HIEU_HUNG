package com.example.demo.model;

import jakarta.persistence.*; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore; 

@Entity
@Table(name = "orders") 
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Temporal(TemporalType.TIMESTAMP) 
    private Date orderDate;

    private String status;
    private double total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderDetail> orderDetails;

    // ===============================================
    // THÊM LIÊN KẾT TỚI USER
    // ===============================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username") // Tên cột trong DB (ví dụ: "username")
    @JsonIgnore // Bỏ qua khi gửi JSON, tránh lỗi lặp vô hạn
    private User user;
    // ===============================================

    public Order() {
        this.orderDetails = new ArrayList<>();
        this.orderDate = new Date(); 
    }

    public Order(int orderId, Date orderDate, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = new ArrayList<>();
        this.total = 0;
    }

    // ... (Getter/Setter cũ của bạn: getOrderId, getOrderDate, ...)
    public int getOrderId() { return orderId; }
    public Date getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public List<OrderDetail> getOrderDetails() { return orderDetails; }
    public double getTotal() { return total; }

    public String getFormattedOrderDate() {
        return Time.dinhDang(orderDate);
    }

    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
    public void setTotal(double total) { this.total = total; }
    
    // ===============================================
    // THÊM GETTER & SETTER CHO USER
    // ===============================================
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // ===============================================

    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
        detail.setOrder(this);
    }

    public double calculateTotal() {
        double sum = 0;
        if (orderDetails != null) {
            for (OrderDetail detail : orderDetails) {
                sum += detail.subTotal();
            }
        }
        this.total = sum;
        return sum;
    }

    @Override
    public String toString() {
        // Cập nhật toString để bao gồm cả username (nếu có)
        String userInfo = (user != null) ? ", user=" + user.getUsername() : "";
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", total=" + total +
                userInfo +
                '}';
    }
}