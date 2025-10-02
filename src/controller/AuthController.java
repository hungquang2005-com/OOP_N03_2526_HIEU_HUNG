package controller;

import model.ListOfUser;
import model.User;
import java.util.Scanner;

public class AuthController {
    private Scanner sc;
    private ListOfUser listOfUser;

    public AuthController(Scanner sc, ListOfUser listOfUser) {
        this.sc = sc;
        this.listOfUser = listOfUser;
    }

    public User handleAuth() {
        while (true) {
            System.out.println("\n1. Đăng ký");
            System.out.println("2. Đăng nhập");
            System.out.println("3. Thoát");
            System.out.print("Lựa chọn: ");
            String mainChoice = sc.nextLine().trim();

            if (mainChoice.equals("1")) {
                System.out.print("Nhập username để đăng ký: ");
                String regUser = sc.nextLine();
                System.out.print("Nhập password: ");
                String regPass = sc.nextLine();
                System.out.print("Nhập lại password: ");
                String regPass2 = sc.nextLine();
                System.out.print("Nhập vai trò (Quản lý/Khách): ");
                String role = sc.nextLine();

                boolean registered = listOfUser.register(regUser, regPass, regPass2, role);
                System.out.println(registered ? "✅ Đăng ký thành công!" : "❌ Đăng ký thất bại.");

            } else if (mainChoice.equals("2")) {
                System.out.print("Username: ");
                String loginUser = sc.nextLine();
                System.out.print("Password: ");
                String loginPass = sc.nextLine();

                User currentUser = listOfUser.login(loginUser, loginPass);
                if (currentUser == null) {
                    System.out.println("❌ Sai thông tin, thử lại!");
                } else {
                    System.out.println("✅ Đăng nhập thành công. Chào " + currentUser.getUserName());
                    currentUser.login();
                    return currentUser;
                }
            } else if (mainChoice.equals("3")) {
                return null;
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }
}

