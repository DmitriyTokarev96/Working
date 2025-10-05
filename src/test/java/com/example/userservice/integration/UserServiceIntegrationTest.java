package com.example.userservice.integration;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для UserService API.
 * Тестирует полную интеграцию между всеми слоями приложения.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    public UserServiceIntegrationTest() {
        // Default constructor
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Integration Test User")
                .email("integration@example.com")
                .age(30)
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test User"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.createdAt").exists());

        // Verify in database
        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findAll().get(0);
        assertThat(savedUser.getName()).isEqualTo("Integration Test User");
        assertThat(savedUser.getEmail()).isEqualTo("integration@example.com");
        assertThat(savedUser.getAge()).isEqualTo(30);
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        User existingUser = User.builder()
                .name("Existing User")
                .email("duplicate@example.com")
                .age(25)
                .build();
        userRepository.save(existingUser);

        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("New User")
                .email("duplicate@example.com") // Same email
                .age(30)
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with email duplicate@example.com already exists"));

        // Verify no new user was created
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .build();
        User savedUser = userRepository.save(user);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        // Given
        User user = User.builder()
                .name("Original Name")
                .email("original@example.com")
                .age(25)
                .build();
        User savedUser = userRepository.save(user);

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("Updated Name")
                .age(30)
                .build();

        // When & Then
        mockMvc.perform(put("/api/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("original@example.com")) // Not changed
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.updatedAt").exists());

        // Verify in database
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getAge()).isEqualTo(30);
        assertThat(updatedUser.getEmail()).isEqualTo("original@example.com");
    }

    @Test
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Given
        User user = User.builder()
                .name("To Delete")
                .email("delete@example.com")
                .age(25)
                .build();
        User savedUser = userRepository.save(user);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", savedUser.getId()))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        // Given
        User user1 = User.builder()
                .name("User 1")
                .email("user1@example.com")
                .age(25)
                .build();
        User user2 = User.builder()
                .name("User 2")
                .email("user2@example.com")
                .age(30)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }
}
