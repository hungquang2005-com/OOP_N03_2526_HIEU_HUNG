package com.example.demo.model;

import java.util.*;

public class Order {
    private int orderId;
    private Date orderDate;
    private String status;
    private List<OrderDetail> orderDetails;
    private double total;

    public Order() {
        this.orderDetails = new ArrayList<>();
    }

    public Order(int orderId, Date orderDate, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = new ArrayList<>();
        this.total = 0;
    }

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

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + Time.dinhDang(orderDate) + // ✅ Dùng Time
                ", status='" + status + '\'' +
                ", total=" + total +
                ", itemCount=" + orderDetails.size() +
                '}';
    }
}