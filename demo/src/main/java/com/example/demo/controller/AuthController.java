package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional; 

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired 
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            if (payload == null) return ResponseEntity.badRequest().body("Dữ liệu đăng nhập không hợp lệ");

            String username = payload.get("username");
            String password = payload.get("password");

            if (username == null || username.trim().isEmpty()) return ResponseEntity.badRequest().body("Username không được để trống");
            if (password == null || password.trim().isEmpty()) return ResponseEntity.badRequest().body("Password không được để trống");

            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getPassword().equals(password)) {
                    System.out.println("✅ User logged in: " + username);
                    return ResponseEntity.ok(user);
                }
            }
            
            System.out.println("❌ Sai username hoặc password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai username hoặc password");

        } catch (Exception e) {
            System.err.println("❌ Error logging in: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            if (payload == null) return ResponseEntity.badRequest().body("Dữ liệu đăng ký không hợp lệ");

            String username = payload.get("username");
            String password = payload.get("password");
            String confirmPassword = payload.get("confirmPassword");
            String role = payload.getOrDefault("role", "Khách");

            if (username == null || username.trim().isEmpty()) return ResponseEntity.badRequest().body("Username không được để trống");
            if (password == null || password.length() < 6) return ResponseEntity.badRequest().body("Password phải có ít nhất 6 ký tự");
            if (!password.equals(confirmPassword)) return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp");

            if (userRepository.existsById(username)) {
                System.out.println("❌ Username đã tồn tại.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username đã tồn tại");
            }

            User newUser = new User(username, password, role);
            User savedUser = userRepository.save(newUser);
            
            System.out.println("✅ Đăng ký thành công: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

        } catch (Exception e) {
            System.err.println("❌ Error registering user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody Map<String, String> payload) {
        try {
            Optional<User> userOpt = userRepository.findById(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user: " + username);
            }

            User user = userOpt.get();
            String newPassword = payload.get("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(newPassword); 
            }

            String newRole = payload.get("role");
            if (newRole != null && !newRole.trim().isEmpty()) {
                user.setRole(newRole);
            }
            
            userRepository.save(user); 
            System.out.println("✏️ Updated user: " + username);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            if (userRepository.existsById(username)) {
                userRepository.deleteById(username); 
                System.out.println("🗑️ Deleted user: " + username);
                return ResponseEntity.ok("Đã xóa user: " + username);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user: " + username);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa user: " + e.getMessage());
        }
    }
}