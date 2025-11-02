package com.example.demo.model;

import jakarta.persistence.*; 
import java.util.Date;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    private double amount;
    private String method;
    private String status;
    private String paymentDate; 

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public Payment() {
        this.status = "Đang xử lý";
        this.timestamp = new Date();
        this.paymentDate = Time.layThoiGianHienTai();
    }

    public Payment(int paymentId, double amount, String method) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.status = "Đang xử lý";
        this.timestamp = new Date();
        this.paymentDate = Time.layThoiGianHienTai();
    }

    public int getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public String getPaymentDate() { return paymentDate; }
    public Date getTimestamp() { return timestamp; }

    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public void setMethod(String method) { this.method = method; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean processPayment() {
        try {
            System.out.println("Processing " + method + " payment of " + amount + " VND...");
            Thread.sleep(100);
            this.status = "Thành công";
            this.paymentDate = Time.layThoiGianHienTai();
            System.out.println("Payment processed successfully at " + paymentDate);
            return true;
        } catch (InterruptedException e) {
            this.status = "Thất bại";
            System.err.println("Payment processing failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}