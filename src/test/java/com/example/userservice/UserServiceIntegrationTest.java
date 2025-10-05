package com.example.userservice;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционные тесты для UserService с Spring Data JPA.
 * Эти тесты проверяют полную интеграцию между Service и Repository слоями.
 */
@DisplayName("UserService Integration Tests")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Создание пользователя")
    void testCreateUser() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Интеграционный тест")
                .email("integration@example.com")
                .age(30)
                .build();
        
        // When - создаем пользователя
        UserDto createdUser = userService.createUser(createUserDto);
        
        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Интеграционный тест");
        assertThat(createdUser.getEmail()).isEqualTo("integration@example.com");
        assertThat(createdUser.getAge()).isEqualTo(30);
        assertThat(createdUser.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Получение пользователя по ID")
    void testGetUserById() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Тест ID")
                .email("testid@example.com")
                .age(25)
                .build();
        UserDto createdUser = userService.createUser(createUserDto);
        
        // When - получаем пользователя по ID
        Optional<UserDto> foundById = userService.getUserById(createdUser.getId());
        
        // Then
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getName()).isEqualTo("Тест ID");
        assertThat(foundById.get().getId()).isEqualTo(createdUser.getId());
    }
    
    @Test
    @DisplayName("Получение пользователя по email")
    void testGetUserByEmail() {
        // Given
        String email = "testemail@example.com";
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Тест Email")
                .email(email)
                .age(25)
                .build();
        UserDto createdUser = userService.createUser(createUserDto);
        
        // When - получаем пользователя по email
        Optional<UserDto> foundByEmail = userService.getUserByEmail(email);
        
        // Then
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(createdUser.getId());
        assertThat(foundByEmail.get().getName()).isEqualTo("Тест Email");
    }
    
    @Test
    @DisplayName("Обновление пользователя")
    void testUpdateUser() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Оригинальное имя")
                .email("update@example.com")
                .age(25)
                .build();
        UserDto createdUser = userService.createUser(createUserDto);
        
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("Обновленное имя")
                .age(31)
                .build();
        
        // When - обновляем пользователя
        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateUserDto);
        
        // Then
        assertThat(updatedUser.getName()).isEqualTo("Обновленное имя");
        assertThat(updatedUser.getAge()).isEqualTo(31);
        assertThat(updatedUser.getEmail()).isEqualTo("update@example.com"); // не изменилось
    }
    
    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteUser() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Для удаления")
                .email("delete@example.com")
                .age(25)
                .build();
        UserDto createdUser = userService.createUser(createUserDto);
        
        // When - удаляем пользователя
        boolean deleted = userService.deleteUser(createdUser.getId());
        
        // Then
        assertThat(deleted).isTrue();
        
        // When - проверяем, что пользователь удален
        Optional<UserDto> deletedUser = userService.getUserById(createdUser.getId());
        
        // Then
        assertThat(deletedUser).isEmpty();
    }
    
    @Test
    @DisplayName("Тест валидации при создании пользователя")
    void testValidationOnUserCreation() {
        // When & Then - тест с пустым именем
        CreateUserDto invalidNameDto = CreateUserDto.builder()
                .name("")
                .email("test@example.com")
                .age(25)
                .build();
        assertThatThrownBy(() -> userService.createUser(invalidNameDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");
        
        // When & Then - тест с невалидным email
        CreateUserDto invalidEmailDto = CreateUserDto.builder()
                .name("Test")
                .email("invalid-email")
                .age(25)
                .build();
        assertThatThrownBy(() -> userService.createUser(invalidEmailDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format: invalid-email");
        
        // When & Then - тест с невалидным возрастом
        CreateUserDto invalidAgeDto = CreateUserDto.builder()
                .name("Test")
                .email("test@example.com")
                .age(-1)
                .build();
        assertThatThrownBy(() -> userService.createUser(invalidAgeDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Age must be between 0 and 150");
    }
    
    @Test
    @DisplayName("Получение всех пользователей")
    void testGetAllUsers() {
        // Given - создаем несколько пользователей
        CreateUserDto user1Dto = CreateUserDto.builder()
                .name("Пользователь 1")
                .email("user1@example.com")
                .age(25)
                .build();
        CreateUserDto user2Dto = CreateUserDto.builder()
                .name("Пользователь 2")
                .email("user2@example.com")
                .age(30)
                .build();
        CreateUserDto user3Dto = CreateUserDto.builder()
                .name("Пользователь 3")
                .email("user3@example.com")
                .age(35)
                .build();
        
        userService.createUser(user1Dto);
        userService.createUser(user2Dto);
        userService.createUser(user3Dto);
        
        // When - получаем всех пользователей
        List<UserDto> allUsers = userService.getAllUsers();
        
        // Then
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(UserDto::getName)
                .containsExactlyInAnyOrder("Пользователь 1", "Пользователь 2", "Пользователь 3");
    }
    
    @Test
    @DisplayName("Проверка уникальности email")
    void testEmailUniqueness() {
        // Given - создаем пользователя
        CreateUserDto firstUserDto = CreateUserDto.builder()
                .name("Первый пользователь")
                .email("unique@example.com")
                .age(25)
                .build();
        userService.createUser(firstUserDto);
        
        CreateUserDto duplicateUserDto = CreateUserDto.builder()
                .name("Дубликат")
                .email("unique@example.com")
                .age(30)
                .build();
        
        // When & Then - проверяем уникальность email
        assertThatThrownBy(() -> userService.createUser(duplicateUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email unique@example.com already exists");
    }
    
    @Test
    @DisplayName("Частичное обновление - только имя")
    void testPartialUpdateName() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Оригинальное имя")
                .email("original@example.com")
                .age(25)
                .build();
        UserDto user = userService.createUser(createUserDto);
        
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("Новое имя")
                .build();
        
        // When - обновляем только имя
        UserDto updatedUser = userService.updateUser(user.getId(), updateUserDto);
        
        // Then
        assertThat(updatedUser.getName()).isEqualTo("Новое имя");
        assertThat(updatedUser.getEmail()).isEqualTo("original@example.com"); // не изменилось
        assertThat(updatedUser.getAge()).isEqualTo(25); // не изменилось
    }
    
    @Test
    @DisplayName("Частичное обновление - только возраст")
    void testPartialUpdateAge() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Тест возраст")
                .email("age@example.com")
                .age(25)
                .build();
        UserDto user = userService.createUser(createUserDto);
        
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .age(30)
                .build();
        
        // When - обновляем только возраст
        UserDto updatedAge = userService.updateUser(user.getId(), updateUserDto);
        
        // Then
        assertThat(updatedAge.getName()).isEqualTo("Тест возраст"); // не изменилось
        assertThat(updatedAge.getEmail()).isEqualTo("age@example.com"); // не изменилось
        assertThat(updatedAge.getAge()).isEqualTo(30); // изменилось
    }
    
    @Test
    @DisplayName("Частичное обновление - только email")
    void testPartialUpdateEmail() {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Тест email")
                .email("old@example.com")
                .age(25)
                .build();
        UserDto user = userService.createUser(createUserDto);
        
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email("new@example.com")
                .build();
        
        // When - обновляем только email
        UserDto updatedEmail = userService.updateUser(user.getId(), updateUserDto);
        
        // Then
        assertThat(updatedEmail.getName()).isEqualTo("Тест email"); // не изменилось
        assertThat(updatedEmail.getEmail()).isEqualTo("new@example.com"); // изменилось
        assertThat(updatedEmail.getAge()).isEqualTo(25); // не изменилось
    }
}



