package controller;

import model.*;
import java.util.*;

public class PaymentController {
    private Scanner sc;
    private Random rand;

    public PaymentController(Scanner sc) {
        this.sc = sc;
        this.rand = new Random();
    }

    public boolean processPayment(Order order) {
        System.out.println("\nChọn phương thức thanh toán:");
        System.out.println("1. Tiền mặt");
        System.out.println("2. QR");
        System.out.println("3. Thẻ tín dụng");
        System.out.print("Lựa chọn: ");

        int payChoice = 1;
        try {
            payChoice = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Lựa chọn không hợp lệ, mặc định tiền mặt.");
        }

        double amount = order.getTotal();
        if (payChoice == 1) {
            Payment payment = new Payment(rand.nextInt(9000) + 1000, amount, "Tiền mặt");
            payment.processPayment();
            System.out.println("✅ Thanh toán tiền mặt " + amount + " VND thành công.");
            return true;
        } else if (payChoice == 2) {
            System.out.println("📱 Mã QR: [*** QR_CODE ***]");
            System.out.print("Xác nhận quét mã thành công (y/n): ");
            String confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("y")) {
                Payment payment = new Payment(rand.nextInt(9000) + 1000, amount, "QR");
                payment.processPayment();
                System.out.println("✅ Thanh toán QR thành công.");
                return true;
            } else {
                System.out.println("❌ Quét mã thất bại.");
                return false;
            }
        } else if (payChoice == 3) {
            System.out.print("Nhập 4 số cuối thẻ: ");
            String last4 = sc.nextLine().trim();
            Payment payment = new Payment(rand.nextInt(9000) + 1000, amount, "Thẻ tín dụng");
            payment.processPayment();
            System.out.println("✅ Thanh toán thẻ (" + last4 + ") thành công.");
            return true;
        } else {
            System.out.println("❌ Lựa chọn không hợp lệ.");
            return false;
        }
    }
}

