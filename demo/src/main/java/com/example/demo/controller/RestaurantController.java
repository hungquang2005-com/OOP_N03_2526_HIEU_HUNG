package com.example.demo.controller;

import java.util.*;

import com.example.demo.model.*;

public class RestaurantController {
    private Scanner sc;
    private ListOfUser listOfUser;
    private RestaurantManagement rm;
    private Set<Integer> usedOrderIds;

    public RestaurantController() {
        sc = new Scanner(System.in);
        listOfUser = new ListOfUser();
        rm = new RestaurantManagement();
        usedOrderIds = new HashSet<>();
    }

    public void start() {
        System.out.println("=== HỆ THỐNG QUẢN LÝ NHÀ HÀNG ===");

        AuthController authCtrl = new AuthController(sc, listOfUser);
        User currentUser = authCtrl.handleAuth();
        if (currentUser == null) {
            System.out.println("👋 Thoát chương trình.");
            return;
        }

        if (currentUser.getRole().equalsIgnoreCase("Quản lý")) {
            rm.generateReport();
            return;
        }

        boolean running = true;
        while (running) {
            TableController tableCtrl = new TableController(sc);
            List<Table> reserved = tableCtrl.reserveTables();

            if (reserved.isEmpty()) continue;

            MenuController menuCtrl = new MenuController();
            OrderController orderCtrl = new OrderController(sc, usedOrderIds);
            PaymentController payCtrl = new PaymentController(sc);

            Order order = orderCtrl.createOrder(menuCtrl.getMenu());
            orderCtrl.printInvoice(order);

            boolean paid = payCtrl.processPayment(order);
            if (paid) {
                rm.createOrder(order);
                rm.generateReport();
            }

            System.out.println("\nBạn muốn làm gì tiếp theo?");
            System.out.println("1. Tiếp tục");
            System.out.println("2. Đăng xuất và thoát");
            String choice = sc.nextLine().trim();
            if (choice.equals("2")) {
                currentUser.logout();
                running = false;
            }
        }
    }
}