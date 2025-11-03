
package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password123", "Khách");
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Khách", user.getRole());
    }

    @Test
    void testSetters() {
        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setRole("Admin");

        assertEquals("newuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("Admin", user.getRole());
    }

    @Test
    void testToString() {
        String expected = "User{username='testuser', role='Khách'}";
        assertEquals(expected, user.toString());
    }

    @Test
    void testDefaultConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
    }
}