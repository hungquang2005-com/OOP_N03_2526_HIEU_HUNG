package com.example.demo.controller;

import com.example.demo.model.ListOfUser;
import com.example.demo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final ListOfUser listOfUser = new ListOfUser();

    // ========== LOGIN ==========
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            // Validation
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu đăng nhập không hợp lệ");
            }

            String username = payload.get("username");
            String password = payload.get("password");

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username không được để trống");
            }

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password không được để trống");
            }

            User currentUser = listOfUser.login(username, password);

            if (currentUser != null) {
                currentUser.login();
                System.out.println("✅ User logged in: " + username);
                return ResponseEntity.ok(currentUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Sai username hoặc password");
            }

        } catch (Exception e) {
            System.err.println("❌ Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đăng nhập: " + e.getMessage());
        }
    }

    // ========== REGISTER (CREATE USER) ==========
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            // Validation
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu đăng ký không hợp lệ");
            }

            String username = payload.get("username");
            String password = payload.get("password");
            String confirmPassword = payload.get("confirmPassword");
            String role = payload.get("role");

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username không được để trống");
            }

            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body("Password phải có ít nhất 6 ký tự");
            }

            if (!password.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp");
            }

            boolean registered = listOfUser.register(username, password, confirmPassword, role);

            if (registered) {
                System.out.println("✅ User registered: " + username);
                return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công!");
            } else {
                return ResponseEntity.badRequest()
                        .body("Đăng ký thất bại. Username có thể đã tồn tại.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đăng ký: " + e.getMessage());
        }
    }

    // ========== READ ALL USERS ==========
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = listOfUser.getAllUsers();
            System.out.println("📖 Reading all users, count: " + users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("❌ Error reading users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc danh sách user: " + e.getMessage());
        }
    }

    // ========== READ USER BY USERNAME ==========
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = listOfUser.findByUsername(username);
            if (user != null) {
                System.out.println("📖 Found user: " + username);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy user: " + username);
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc user: " + e.getMessage());
        }
    }

    // ========== UPDATE USER ==========
    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @RequestBody Map<String, String> payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu cập nhật không hợp lệ");
            }

            User user = listOfUser.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy user: " + username);
            }

            // Update password if provided
            String newPassword = payload.get("password");
            if (newPassword != null && newPassword.length() >= 6) {
                user.setPassword(newPassword);
            }

            // Update role if provided
            String newRole = payload.get("role");
            if (newRole != null && !newRole.trim().isEmpty()) {
                user.setRole(newRole);
            }

            System.out.println("✏️ Updated user: " + username);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật user: " + e.getMessage());
        }
    }

    // ========== DELETE USER ==========
    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            boolean deleted = listOfUser.deleteUser(username);
            if (deleted) {
                System.out.println("🗑️ Deleted user: " + username);
                return ResponseEntity.ok("Đã xóa user: " + username);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy user: " + username);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa user: " + e.getMessage());
        }
    }
}