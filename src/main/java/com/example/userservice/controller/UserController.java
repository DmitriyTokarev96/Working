package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserResourceDto;
import com.example.userservice.service.HateoasService;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller для управления пользователями.
 * Предоставляет CRUD операции через HTTP API с поддержкой HATEOAS.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API for managing users with HATEOAS support")
public class UserController {
    
    private final UserService userService;
    private final HateoasService hateoasService;
    
    /**
     * Создает нового пользователя.
     * 
     * @param createUserDto данные для создания пользователя
     * @return созданный пользователь с HATEOAS ссылками
     */
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user and sends a notification via Kafka"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResourceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<UserResourceDto> createUser(
            @Parameter(description = "User data for creation", required = true)
            @Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Creating user: {}", createUserDto);
        UserDto createdUser = userService.createUser(createUserDto);
        UserResourceDto userResource = hateoasService.toUserResource(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResource);
    }
    
    /**
     * Получает пользователя по ID.
     * 
     * @param id ID пользователя
     * @return пользователь с HATEOAS ссылками или 404 если не найден
     */
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResourceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResourceDto> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        return userService.getUserById(id)
                .map(user -> {
                    UserResourceDto userResource = hateoasService.toUserResource(user);
                    return ResponseEntity.ok(userResource);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получает пользователя по email.
     * 
     * @param email email пользователя
     * @return пользователь с HATEOAS ссылками или 404 если не найден
     */
    @Operation(
        summary = "Get user by email",
        description = "Retrieves a user by their email address"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResourceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResourceDto> getUserByEmail(
            @Parameter(description = "User email address", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        log.info("Getting user by email: {}", email);
        return userService.getUserByEmail(email)
                .map(user -> {
                    UserResourceDto userResource = hateoasService.toUserResource(user);
                    return ResponseEntity.ok(userResource);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получает всех пользователей.
     * 
     * @return список всех пользователей с HATEOAS ссылками
     */
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users with HATEOAS links"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of users retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CollectionModel.class)
        )
    )
    @GetMapping
    public ResponseEntity<CollectionModel<UserResourceDto>> getAllUsers() {
        log.info("Getting all users");
        List<UserDto> users = userService.getAllUsers();
        List<UserResourceDto> userResources = users.stream()
                .map(hateoasService::toUserResource)
                .collect(Collectors.toList());
        
        CollectionModel<UserResourceDto> collection = CollectionModel.of(userResources);
        
        // Добавляем ссылку на создание нового пользователя
        try {
            Link createLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).createUser(null)
            ).withRel("create-user");
            collection.add(createLink);
        } catch (Exception e) {
            // Игнорируем ошибки при создании ссылок
        }
        
        return ResponseEntity.ok(collection);
    }
    
    /**
     * Обновляет пользователя.
     * 
     * @param id ID пользователя
     * @param updateUserDto данные для обновления
     * @return обновленный пользователь с HATEOAS ссылками или 404 если не найден
     */
    @Operation(
        summary = "Update user",
        description = "Updates an existing user with the provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResourceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResourceDto> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "User data for update", required = true)
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        log.info("Updating user with ID: {}, data: {}", id, updateUserDto);
        try {
            UserDto updatedUser = userService.updateUser(id, updateUserDto);
            UserResourceDto userResource = hateoasService.toUserResource(updatedUser);
            return ResponseEntity.ok(userResource);
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
    @Operation(
        summary = "Delete user",
        description = "Deletes a user by ID and sends a notification via Kafka"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
