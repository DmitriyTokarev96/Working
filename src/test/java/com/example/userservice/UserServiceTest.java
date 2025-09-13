package com.example.userservice;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для UserService.
 * Использует H2 in-memory базу данных для тестирования.
 */
public class UserServiceTest {
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test data
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            userService.deleteUser(user.getId());
        }
    }
    
    @Test
    void testCreateUser() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        Integer age = 30;
        
        // When
        User user = userService.createUser(name, email, age);
        
        // Then
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(age, user.getAge());
        assertNotNull(user.getCreatedAt());
    }
    
    @Test
    void testCreateUserWithNullAge() {
        // Given
        String name = "Jane Doe";
        String email = "jane.doe@example.com";
        
        // When
        User user = userService.createUser(name, email, null);
        
        // Then
        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertNull(user.getAge());
    }
    
    @Test
    void testCreateUserWithDuplicateEmail() {
        // Given
        String name1 = "John Doe";
        String name2 = "Jane Doe";
        String email = "duplicate@example.com";
        Integer age = 30;
        
        // When
        userService.createUser(name1, email, age);
        
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(name2, email, age);
        });
    }
    
    @Test
    void testCreateUserWithInvalidEmail() {
        // Given
        String name = "John Doe";
        String invalidEmail = "invalid-email";
        Integer age = 30;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(name, invalidEmail, age);
        });
    }
    
    @Test
    void testCreateUserWithInvalidAge() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        Integer invalidAge = -5;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(name, email, invalidAge);
        });
    }
    
    @Test
    void testGetUserById() {
        // Given
        User createdUser = userService.createUser("John Doe", "john.doe@example.com", 30);
        Long userId = createdUser.getId();
        
        // When
        Optional<User> foundUser = userService.getUserById(userId);
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(createdUser.getId(), foundUser.get().getId());
        assertEquals(createdUser.getName(), foundUser.get().getName());
        assertEquals(createdUser.getEmail(), foundUser.get().getEmail());
    }
    
    @Test
    void testGetUserByIdNotFound() {
        // Given
        Long nonExistentId = 999L;
        
        // When
        Optional<User> foundUser = userService.getUserById(nonExistentId);
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testGetUserByEmail() {
        // Given
        User createdUser = userService.createUser("John Doe", "john.doe@example.com", 30);
        String email = createdUser.getEmail();
        
        // When
        Optional<User> foundUser = userService.getUserByEmail(email);
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(createdUser.getId(), foundUser.get().getId());
        assertEquals(createdUser.getEmail(), foundUser.get().getEmail());
    }
    
    @Test
    void testGetAllUsers() {
        // Given
        userService.createUser("John Doe", "john.doe@example.com", 30);
        userService.createUser("Jane Doe", "jane.doe@example.com", 25);
        userService.createUser("Bob Smith", "bob.smith@example.com", 35);
        
        // When
        List<User> users = userService.getAllUsers();
        
        // Then
        assertEquals(3, users.size());
    }
    
    @Test
    void testUpdateUser() {
        // Given
        User createdUser = userService.createUser("John Doe", "john.doe@example.com", 30);
        Long userId = createdUser.getId();
        
        // When
        User updatedUser = userService.updateUser(userId, "John Updated", "john.updated@example.com", 31);
        
        // Then
        assertEquals("John Updated", updatedUser.getName());
        assertEquals("john.updated@example.com", updatedUser.getEmail());
        assertEquals(Integer.valueOf(31), updatedUser.getAge());
    }
    
    @Test
    void testUpdateUserPartial() {
        // Given
        User createdUser = userService.createUser("John Doe", "john.doe@example.com", 30);
        Long userId = createdUser.getId();
        
        // When - update only name
        User updatedUser = userService.updateUser(userId, "John Updated", null, null);
        
        // Then
        assertEquals("John Updated", updatedUser.getName());
        assertEquals("john.doe@example.com", updatedUser.getEmail()); // unchanged
        assertEquals(Integer.valueOf(30), updatedUser.getAge()); // unchanged
    }
    
    @Test
    void testUpdateUserNotFound() {
        // Given
        Long nonExistentId = 999L;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(nonExistentId, "John Updated", "john.updated@example.com", 31);
        });
    }
    
    @Test
    void testDeleteUser() {
        // Given
        User createdUser = userService.createUser("John Doe", "john.doe@example.com", 30);
        Long userId = createdUser.getId();
        
        // When
        boolean deleted = userService.deleteUser(userId);
        
        // Then
        assertTrue(deleted);
        Optional<User> foundUser = userService.getUserById(userId);
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testDeleteUserNotFound() {
        // Given
        Long nonExistentId = 999L;
        
        // When
        boolean deleted = userService.deleteUser(nonExistentId);
        
        // Then
        assertFalse(deleted);
    }
    
    @Test
    void testDeleteUserWithInvalidId() {
        // Given
        Long invalidId = -1L;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(invalidId);
        });
    }
}
