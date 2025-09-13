package com.example.userservice;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест для UserServiceApplication.
 * Тестирует основную функциональность без подключения к базе данных.
 */
public class UserServiceApplicationTest {
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        // Note: This test requires a database connection
        // For unit testing without database, mock the UserService
        userService = new UserService();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test data if needed
    }
    
    @Test
    void testUserServiceInitialization() {
        // Test that UserService can be initialized
        assertNotNull(userService);
    }
    
    @Test
    void testUserEntityCreation() {
        // Test User entity creation without database
        User user = new User("Test User", "test@example.com", 25);
        
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Integer.valueOf(25), user.getAge());
        assertNotNull(user.getCreatedAt());
    }
    
    @Test
    void testUserEntityValidation() {
        // Test User entity validation
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);
        
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Integer.valueOf(25), user.getAge());
    }
    
    @Test
    void testUserEntityToString() {
        // Test User toString method
        User user = new User("Test User", "test@example.com", 25);
        String userString = user.toString();
        
        assertTrue(userString.contains("Test User"));
        assertTrue(userString.contains("test@example.com"));
        assertTrue(userString.contains("25"));
    }
    
    @Test
    void testUserEntityEquals() {
        // Test User equals method
        User user1 = new User("Test User", "test@example.com", 25);
        User user2 = new User("Test User", "test@example.com", 25);
        User user3 = new User("Different User", "different@example.com", 30);
        
        // Set same ID for comparison
        user1.setId(1L);
        user2.setId(1L);
        user3.setId(2L);
        
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }
    
    @Test
    void testUserEntityHashCode() {
        // Test User hashCode method
        User user1 = new User("Test User", "test@example.com", 25);
        User user2 = new User("Test User", "test@example.com", 25);
        
        user1.setId(1L);
        user2.setId(1L);
        
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
