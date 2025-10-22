package com.example.userservice.service;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервисный класс для бизнес-логики пользователей.
 * Предоставляет высокоуровневые операции для управления пользователями.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final EventPublisherService eventPublisherService;
    
    /**
     * Создает нового пользователя.
     * 
     * @param createUserDto данные для создания пользователя
     * @return созданный пользователь в виде DTO
     * @throws IllegalArgumentException если пользователь с таким email уже существует
     */
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Creating user with name: {}, email: {}, age: {}", 
                createUserDto.getName(), createUserDto.getEmail(), createUserDto.getAge());
        
        // Проверяем, существует ли пользователь с таким email
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + createUserDto.getEmail() + " already exists");
        }
        
        User user = User.builder()
                .name(createUserDto.getName())
                .email(createUserDto.getEmail())
                .age(createUserDto.getAge())
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Отправляем событие о создании пользователя
        eventPublisherService.publishUserEvent("CREATE", savedUser.getEmail(), savedUser.getName());
        
        return convertToDto(savedUser);
    }
    
    /**
     * Получает пользователя по ID.
     * 
     * @param id ID пользователя
     * @return Optional содержащий пользователя в виде DTO если найден
     */
    public Optional<UserDto> getUserById(Long id) {
        log.info("Getting user by ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + id);
        }
        return userRepository.findById(id)
                .map(this::convertToDto);
    }
    
    /**
     * Получает пользователя по email.
     * 
     * @param email email пользователя
     * @return Optional содержащий пользователя в виде DTO если найден
     */
    public Optional<UserDto> getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }
    
    /**
     * Получает всех пользователей.
     * 
     * @return список всех пользователей в виде DTO
     */
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновляет информацию о пользователе.
     * 
     * @param id ID пользователя
     * @param updateUserDto данные для обновления
     * @return обновленный пользователь в виде DTO
     * @throws IllegalArgumentException если пользователь не найден или валидация не прошла
     */
    @Transactional
    public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        log.info("Updating user with ID: {}, data: {}", id, updateUserDto);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + id);
        }
        
        // Получаем существующего пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        
        // Обновляем поля, если они предоставлены
        if (updateUserDto.getName() != null && !updateUserDto.getName().trim().isEmpty()) {
            existingUser.setName(updateUserDto.getName().trim());
        }
        
        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().trim().isEmpty()) {
            // Проверяем, отличается ли новый email и не занят ли он
            if (!updateUserDto.getEmail().equals(existingUser.getEmail()) && 
                userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), id)) {
                throw new IllegalArgumentException("User with email " + updateUserDto.getEmail() + " already exists");
            }
            existingUser.setEmail(updateUserDto.getEmail().trim());
        }
        
        if (updateUserDto.getAge() != null) {
            existingUser.setAge(updateUserDto.getAge());
        }
        
        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }
    
    /**
     * Удаляет пользователя по ID.
     * 
     * @param id ID пользователя
     * @return true если пользователь был удален, false если не найден
     */
    @Transactional
    public boolean deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + id);
        }
        
        if (userRepository.existsById(id)) {
            // Получаем данные пользователя перед удалением для отправки события
            User userToDelete = userRepository.findById(id).orElse(null);
            if (userToDelete != null) {
                userRepository.deleteById(id);
                // Отправляем событие об удалении пользователя
                eventPublisherService.publishUserEvent("DELETE", userToDelete.getEmail(), userToDelete.getName());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Конвертирует User entity в UserDto.
     * 
     * @param user пользователь
     * @return DTO пользователя
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
