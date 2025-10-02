package model;
import java.util.ArrayList;
import java.util.List;

public class User extends Person implements PeopleInterface {
    private String userName;
    private String password;
    private String role;
    private String loginDate; 

    private static List<User> users = new ArrayList<>();

    public User(String userName, String password, String role) {
        super("", "", "");
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public User(String userName, String password, String role,
                String identity, String fullName, String dateOfBirth) {
        super(identity, fullName, dateOfBirth);
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    @Override
    public void setInfon(String identity, String fullName, String dateOfBirth) {
        super.setInfon(identity, fullName, dateOfBirth);
    }

    @Override
    public String getInfo(String identity) {
        if (super.getIdentity() != null && super.getIdentity().equals(identity)) {
            return "Username: " + userName +
                   ", FullName: " + super.getFullName() +
                   ", DOB: " + super.getDateOfBirth() +
                   ", Identity: " + super.getIdentity() +
                   ", Role: " + role +
                   ", LoginDate: " + loginDate;
        }
        return "Không tìm thấy thông tin user với Identity: " + identity;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getDate() { return loginDate; }

    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setDate(String loginDate) { this.loginDate = loginDate; }

    public void login() {
        this.loginDate = Time.layThoiGianHienTai(); 
        System.out.println(userName + " logged in at " + loginDate);
    }

    public void logout() {
        System.out.println(userName + " logged out.");
    }

    // ========== CRUD ==========
    public static void create(User u) {
        users.add(u);
    }

    public static List<User> readAll() {
        return users;
    }

    public static User readByUsername(String username) {
        for (User u : users) {
            if (u.getUserName().equals(username)) return u;
        }
        return null;
    }

    public static void update(String username, String newPass, String newRole) {
        User u = readByUsername(username);
        if (u != null) {
            u.setPassword(newPass);
            u.setRole(newRole);
        }
    }

    public static void delete(String username) {
        users.removeIf(u -> u.getUserName().equals(username));
    }
}
