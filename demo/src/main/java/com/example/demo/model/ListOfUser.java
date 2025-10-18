package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class ListOfUser {
    private List<User> users;

    public ListOfUser() {
        users = new ArrayList<>();
    }

    public boolean register(String username, String password, String confirmPassword, String role) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Username không được để trống.");
            return false;
        }

        if (password == null || password.length() < 6) {
            System.out.println("❌ Password phải có ít nhất 6 ký tự.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("❌ Mật khẩu xác nhận không khớp.");
            return false;
        }

        // Check duplicate
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("❌ Username đã tồn tại.");
                return false;
            }
        }

        // Create user
        User newUser = new User(username, password, role != null ? role : "Khách");
        users.add(newUser);
        System.out.println("✅ Đăng ký thành công: " + username);
        return true;
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            System.out.println("❌ Username và password không được để trống.");
            return null;
        }

        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                System.out.println("✅ Đăng nhập thành công: " + username);
                return u;
            }
        }
        System.out.println("❌ Sai username hoặc password.");
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean deleteUser(String username) {
        return users.removeIf(u -> u.getUsername().equals(username));
    }

    public User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}