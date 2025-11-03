package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123", "Khách");
    }

    // ========== LOGIN TESTS ==========
    @Test
    void login_Success() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void login_WrongPassword() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Sai username hoặc password"));
    }

    @Test
    void login_UserNotFound() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nonexistent");
        loginRequest.put("password", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_EmptyUsername() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username không được để trống"));
    }

    @Test
    void login_EmptyPassword() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password không được để trống"));
    }

    // ========== REGISTER TESTS ==========
    @Test
    void register_Success() throws Exception {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("password", "password123");
        registerRequest.put("confirmPassword", "password123");
        registerRequest.put("role", "Khách");

        when(userRepository.existsById("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UsernameExists() throws Exception {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "testuser");
        registerRequest.put("password", "password123");
        registerRequest.put("confirmPassword", "password123");

        when(userRepository.existsById("testuser")).thenReturn(true);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username đã tồn tại"));
    }

    @Test
    void register_PasswordTooShort() throws Exception {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("password", "12345");
        registerRequest.put("confirmPassword", "12345");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password phải có ít nhất 6 ký tự"));
    }

    @Test
    void register_PasswordMismatch() throws Exception {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("password", "password123");
        registerRequest.put("confirmPassword", "password456");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mật khẩu xác nhận không khớp"));
    }

    // ========== GET ALL USERS TEST ==========
    @Test
    void getAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser, new User("user2", "pass", "Admin"));
        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ========== UPDATE USER TEST ==========
    @Test
    void updateUser_Success() throws Exception {
        Map<String, String> updateRequest = new HashMap<>();
        updateRequest.put("password", "newpassword");
        updateRequest.put("role", "Admin");

        when(userRepository.findById("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/users/testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        Map<String, String> updateRequest = new HashMap<>();
        updateRequest.put("password", "newpassword");

        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE USER TEST ==========
    @Test
    void deleteUser_Success() throws Exception {
        when(userRepository.existsById("testuser")).thenReturn(true);

        mockMvc.perform(delete("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("Đã xóa user: testuser"));

        verify(userRepository, times(1)).deleteById("testuser");
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/users/nonexistent"))
                .andExpect(status().isNotFound());
    }
}