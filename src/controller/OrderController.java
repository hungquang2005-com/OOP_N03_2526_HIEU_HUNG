package controller;

import model.*;
import java.util.*;

public class OrderController {
    private Scanner sc;
    private Random rand;
    private Set<Integer> usedOrderIds;

    public OrderController(Scanner sc, Set<Integer> usedOrderIds) {
        this.sc = sc;
        this.rand = new Random();
        this.usedOrderIds = usedOrderIds;
    }

    public Order createOrder(List<Food> menu) {
        int orderId;
        do {
            orderId = rand.nextInt(9000) + 1000;
        } while (usedOrderIds.contains(orderId));
        usedOrderIds.add(orderId);

        Order order = new Order(orderId, new Date(), "Chưa thanh toán");
        System.out.println("\nTạo hóa đơn với ID: " + orderId);

        int menuChoice;
        do {
            System.out.println("\n=== MENU ===");
            for (Food f : menu) {
                System.out.println(f.getFoodID() + ". " + f.getName() + " - " + f.getPrice() + " VND");
            }
            System.out.print("Nhập ID món (0 để xong): ");
            try {
                menuChoice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Nhập không hợp lệ, thử lại.");
                menuChoice = -1;
                continue;
            }

            if (menuChoice != 0) {
                System.out.print("Số lượng: ");
                int qty;
                try {
                    qty = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Số lượng không hợp lệ, bỏ qua lựa chọn.");
                    continue;
                }

                Food selected = null;
                for (Food f : menu) {
                    if (f.getFoodID() == menuChoice) {
                        selected = f;
                        break;
                    }
                }

                if (selected != null) {
                    order.addOrderDetail(new OrderDetail(selected, qty));
                    System.out.println("✅ Đã thêm " + qty + " x " + selected.getName());
                } else {
                    System.out.println("❌ Món không tồn tại!");
                }
            }
        } while (menuChoice != 0);

        double total = order.calculateTotal();
        order.setTotal(total);

        // ===== Nhập mã giảm giá =====
        applyDiscount(order);

        return order;
    }

    // ===== Xử lý giảm giá =====
private void applyDiscount(Order order) {
    boolean discountApplied = false;
    while (!discountApplied) {
        System.out.print("\nNhập mã giảm giá (Enter nếu không có): ");
        String discountCode = sc.nextLine().trim();

        if (discountCode.isEmpty()) {
            System.out.println("Bạn không nhập mã giảm giá.");
            System.out.println("1. Nhập lại mã");
            System.out.println("2. Bỏ qua và tiếp tục thanh toán");
            System.out.print("Lựa chọn: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("1")) {
                continue; 
            } else if (choice.equals("2")) {
                break; 
            } else {
                System.out.println("❌ Lựa chọn không hợp lệ, quay lại.");
            }
        } else {
            if (discountCode.equalsIgnoreCase("DISCOUNT10")) {
                double newTotal = order.calculateTotal() * 0.9; // giảm 10%
                order.setTotal(newTotal);
                System.out.println("🎉 Áp dụng mã giảm giá thành công! Tổng sau giảm: " + newTotal + " VND");
                discountApplied = true;
            } else {
                System.out.println("❌ Mã giảm giá không hợp lệ! Vui lòng nhập lại.");
            }
        }
    }
}


    public void printInvoice(Order order) {
        System.out.println("\n=== HÓA ĐƠN ===");
        for (OrderDetail d : order.getOrderDetails()) {
            System.out.println(d.getQuantity() + " x " + d.getFood().getName() + " = " + d.subTotal() + " VND");
        }
        System.out.println("Tổng cộng: " + order.getTotal() + " VND");
    }
}
