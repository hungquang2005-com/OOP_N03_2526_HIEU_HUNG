package model;
import java.util.ArrayList;
import java.util.List;

public class ListOfUser {
    private List<User> users;

    public ListOfUser() {
        users = new ArrayList<>();
    }

    // Đăng ký user mới (có xác nhận mật khẩu)
    public boolean register(String username, String password, String confirmPassword, String role) {
        if (!password.equals(confirmPassword)) {
            System.out.println("❌ Mật khẩu xác nhận không khớp. Hãy thử lại.");
            return false;
        }

        // Kiểm tra username đã tồn tại chưa
        for (User u : users) {
            if (u.getUserName().equals(username)) {
                System.out.println("❌ Username đã tồn tại. Vui lòng chọn tên khác.");
                return false;
            }
        }

        User newUser = new User(username, password, role);
        users.add(newUser);
        System.out.println("✅ Đăng ký thành công! Bạn có thể đăng nhập.");
        return true;
    }

    // Overload: Đăng ký nhanh (không cần confirm)
    public boolean register(String username, String password, String role) {
        return register(username, password, password, role);
    }

    // Đăng nhập
    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUserName().equals(username) && u.getPassword().equals(password)) {
                System.out.println("✅ Đăng nhập thành công!");
                return u;
            }
        }
        System.out.println("❌ Sai tên đăng nhập hoặc mật khẩu.");
        return null;
    }

    public List<User> getUsers() {
        return users;
    }
}
