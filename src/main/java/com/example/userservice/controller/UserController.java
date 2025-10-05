package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller для управления пользователями.
 * Предоставляет CRUD операции через HTTP API.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    public UserController() {
        // Default constructor for Spring
    }
    
    /**
     * Создает нового пользователя.
     * 
     * @param createUserDto данные для создания пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Creating user: {}", createUserDto);
        UserDto createdUser = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * Получает пользователя по ID.
     * 
     * @param id ID пользователя
     * @return пользователь или 404 если не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получает пользователя по email.
     * 
     * @param email email пользователя
     * @return пользователь или 404 если не найден
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получает всех пользователей.
     * 
     * @return список всех пользователей
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Getting all users");
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Обновляет пользователя.
     * 
     * @param id ID пользователя
     * @param updateUserDto данные для обновления
     * @return обновленный пользователь или 404 если не найден
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, 
                                           @Valid @RequestBody UpdateUserDto updateUserDto) {
        log.info("Updating user with ID: {}, data: {}", id, updateUserDto);
        try {
            UserDto updatedUser = userService.updateUser(id, updateUserDto);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.warn("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Удаляет пользователя.
     * 
     * @param id ID пользователя
     * @return 204 если удален, 404 если не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
