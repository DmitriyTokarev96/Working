package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для UserController с использованием MockMvc.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    
    public UserControllerTest() {
        // Default constructor
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService).createUser(any(CreateUserDto.class));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .name("") // Invalid: empty name
                .email("invalid-email") // Invalid: invalid email format
                .age(-1) // Invalid: negative age
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.age").exists());

        verify(userService, never()).createUser(any(CreateUserDto.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.getUserById(1L)).thenReturn(Optional.of(userDto));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(userDto));

        // When & Then
        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        // Given
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("User 1")
                .email("user1@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("User 2")
                .email("user2@example.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("User 2"));

        verify(userService).getAllUsers();
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("Updated User")
                .age(30)
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Updated User")
                .email("test@example.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.updateUser(1L, updateUserDto)).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userService).updateUser(1L, updateUserDto);
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("Updated User")
                .build();

        when(userService.updateUser(1L, updateUserDto))
                .thenThrow(new IllegalArgumentException("User not found with ID: 1"));

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(1L, updateUserDto);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(userService.deleteUser(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.deleteUser(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(1L);
    }
}
