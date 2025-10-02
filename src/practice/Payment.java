package practice;

public class Payment {
    private int paymentID;
    private double amount;
    private String method;

    public Payment(int paymentID, double amount, String method) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.method = method;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public void processPayment() {
        System.out.println("Processing " + method + " payment of " + amount + "...");
    }
}

