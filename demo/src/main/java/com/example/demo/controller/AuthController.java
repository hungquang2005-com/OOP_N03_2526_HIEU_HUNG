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
import java.util.Date; // Đảm bảo đã import Date

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired 
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {

            String username = payload.get("username");
            String password = payload.get("password");


            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getPassword().equals(password)) {
                    
                    // START: KIỂM TRA TRẠNG THÁI VÔ HIỆU HÓA TRƯỚC KHI CHO ĐĂNG NHẬP
                    if (!user.isEnabled()) {
                        System.out.println("❌ Login failed: User is disabled " + username);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản của bạn đã bị vô hiệu hóa.");
                    }
                    // END: KIỂM TRA VÔ HIỆU HÓA

                    System.out.println("✅ User logged in: " + username);
                    
                    user.setLastLoginDate(new Date()); 
                    userRepository.save(user); 
                    
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

    // ===========================================
    // HÀM ĐĂNG KÝ (FIX LỖI THIẾU ENDPOINT)
    // ===========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            if (payload == null) {
                return ResponseEntity.badRequest().body("Dữ liệu đăng ký không hợp lệ");
            }

            String username = payload.get("username");
            String password = payload.get("password");
            String role = payload.getOrDefault("role", "USER"); // Mặc định role là 'USER'

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username không được để trống");
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password không được để trống");
            }

            // 1. KIỂM TRA TÊN USER ĐÃ TỒN TẠI
            if (userRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username đã tồn tại.");
            }

            // 2. TẠO USER MỚI
            User newUser = new User(username, password, role);
            newUser.setEnabled(true); // Mặc định tài khoản mới là kích hoạt

            // 3. LƯU VÀO DATABASE
            userRepository.save(newUser);
            
            System.out.println("✅ User registered: " + newUser.getUsername() + " with role: " + newUser.getRole());
            return ResponseEntity.ok("Đăng ký thành công! Username: " + username);

        } catch (Exception e) {
            System.err.println("❌ Error registering user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đăng ký user: " + e.getMessage());
        }
    }
    // ===========================================
    

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll(); 
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    // Admin: Cập nhật thông tin user (password/role)
    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody Map<String, String> payload) {
        try {
            Optional<User> userOpt = userRepository.findById(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user: " + username);
            }

            User user = userOpt.get();
            
            // Cập nhật Password (nếu có)
            String newPassword = payload.get("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(newPassword); 
            }

            // Cập nhật Role (nếu có)
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


    @PutMapping("/users/{username}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String username, @RequestBody Map<String, Boolean> payload) {
        try {
            Optional<User> userOpt = userRepository.findById(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user: " + username);
            }

            User user = userOpt.get();
            if (payload == null || !payload.containsKey("enabled")) {
                return ResponseEntity.badRequest().body("Dữ liệu trạng thái không hợp lệ.");
            }
            
            boolean newStatus = payload.get("enabled"); 

            user.setEnabled(newStatus);
            userRepository.save(user); 

            System.out.println("⚙️ User " + username + " status changed to: " + (newStatus ? "ENABLED" : "DISABLED"));
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            System.err.println("❌ Error changing user status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái user: " + e.getMessage());
        }
    }

    // Admin: Xóa user
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