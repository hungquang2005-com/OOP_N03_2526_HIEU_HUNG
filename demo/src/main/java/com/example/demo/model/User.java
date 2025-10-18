package com.example.demo.model;

public class User {
    private String username;
    private String password;
    private String role;
    private boolean loggedIn;
    private String loginDate; 

    public User() {
        this.loggedIn = false;
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.loggedIn = false;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return loggedIn; }
    public String getLoginDate() { return loginDate; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    public void setLoginDate(String loginDate) { this.loginDate = loginDate; }

    public void login() {
        this.loggedIn = true;
        this.loginDate = Time.layThoiGianHienTai();
        System.out.println(username + " logged in at " + loginDate);
    }

    public void logout() {
        this.loggedIn = false;
        System.out.println(username + " logged out at " + Time.layThoiGianHienTai()); 
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", loggedIn=" + loggedIn +
                ", loginDate='" + loginDate + '\'' +
                '}';
    }
}