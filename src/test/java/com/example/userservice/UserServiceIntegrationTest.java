package com.example.userservice;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционные тесты для UserService с реальной H2 in-memory базой данных.
 * Эти тесты проверяют полную интеграцию между Service и DAO слоями.
 */
@DisplayName("UserService Integration Tests")
public class UserServiceIntegrationTest extends BaseTest {
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        setUpIsolation();
        userService = new UserService();
    }
    
    @Test
    @DisplayName("Полный цикл создания и получения пользователя")
    void testFullUserLifecycle() {
        // Given
        String name = "Интеграционный тест";
        String email = "integration@example.com";
        Integer age = 30;
        
        // When - создаем пользователя
        User createdUser = userService.createUser(name, email, age);
        
        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(name);
        assertThat(createdUser.getEmail()).isEqualTo(email);
        assertThat(createdUser.getAge()).isEqualTo(age);
        assertThat(createdUser.getCreatedAt()).isNotNull();
        
        // When - получаем пользователя по ID
        Optional<User> foundById = userService.getUserById(createdUser.getId());
        
        // Then
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getName()).isEqualTo(name);
        
        // When - получаем пользователя по email
        Optional<User> foundByEmail = userService.getUserByEmail(email);
        
        // Then
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(createdUser.getId());
        
        // When - обновляем пользователя
        User updatedUser = userService.updateUser(createdUser.getId(), "Обновленное имя", null, 31);
        
        // Then
        assertThat(updatedUser.getName()).isEqualTo("Обновленное имя");
        assertThat(updatedUser.getAge()).isEqualTo(31);
        
        // When - удаляем пользователя
        boolean deleted = userService.deleteUser(createdUser.getId());
        
        // Then
        assertThat(deleted).isTrue();
        
        // When - проверяем, что пользователь удален
        Optional<User> deletedUser = userService.getUserById(createdUser.getId());
        
        // Then
        assertThat(deletedUser).isEmpty();
    }
    
    @Test
    @DisplayName("Тест валидации при создании пользователя")
    void testValidationOnUserCreation() {
        // When & Then - тест с null именем
        assertThatThrownBy(() -> userService.createUser(null, "test@example.com", 25))
                .isInstanceOf(NullPointerException.class);
        
        // When & Then - тест с пустым именем
        assertThatThrownBy(() -> userService.createUser("", "test@example.com", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");
        
        // When & Then - тест с невалидным email
        assertThatThrownBy(() -> userService.createUser("Test", "invalid-email", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format: invalid-email");
        
        // When & Then - тест с невалидным возрастом
        assertThatThrownBy(() -> userService.createUser("Test", "test@example.com", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Age must be between 0 and 150");
    }
    
    @Test
    @DisplayName("Тест работы с множественными пользователями")
    void testMultipleUsers() {
        // Given - создаем несколько пользователей
        User user1 = userService.createUser("Пользователь 1", "user1@example.com", 25);
        User user2 = userService.createUser("Пользователь 2", "user2@example.com", 30);
        User user3 = userService.createUser("Пользователь 3", "user3@example.com", 35);
        
        // When - получаем всех пользователей
        List<User> allUsers = userService.getAllUsers();
        
        // Then
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(User::getName)
                .containsExactlyInAnyOrder("Пользователь 1", "Пользователь 2", "Пользователь 3");
        
        // When - проверяем уникальность email
        assertThatThrownBy(() -> userService.createUser("Дубликат", "user1@example.com", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email user1@example.com already exists");
    }
    
    @Test
    @DisplayName("Тест частичного обновления пользователя")
    void testPartialUserUpdate() {
        // Given
        User user = userService.createUser("Оригинальное имя", "original@example.com", 25);
        
        // When - обновляем только имя
        User updatedUser = userService.updateUser(user.getId(), "Новое имя", null, null);
        
        // Then
        assertThat(updatedUser.getName()).isEqualTo("Новое имя");
        assertThat(updatedUser.getEmail()).isEqualTo("original@example.com"); // не изменилось
        assertThat(updatedUser.getAge()).isEqualTo(25); // не изменилось
        
        // When - обновляем только возраст
        User updatedAge = userService.updateUser(user.getId(), null, null, 30);
        
        // Then
        assertThat(updatedAge.getName()).isEqualTo("Новое имя"); // не изменилось
        assertThat(updatedAge.getEmail()).isEqualTo("original@example.com"); // не изменилось
        assertThat(updatedAge.getAge()).isEqualTo(30); // изменилось
    }
}
