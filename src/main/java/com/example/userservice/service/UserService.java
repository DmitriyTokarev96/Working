package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный класс для бизнес-логики пользователей.
 * Предоставляет высокоуровневые операции для управления пользователями.
 */
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    
    public UserService() {
        this.userDao = new UserDao();
    }
    
    /**
     * Создает нового пользователя с валидацией.
     * 
     * @param name имя пользователя
     * @param email email пользователя
     * @param age возраст пользователя
     * @return созданный пользователь
     * @throws IllegalArgumentException если валидация не прошла
     */
    public User createUser(String name, String email, Integer age) {
        logger.info("Creating user with name: {}, email: {}, age: {}", name, email, age);
        
        // Валидация
        validateUserInput(name, email, age);
        
        // Проверяем, существует ли пользователь с таким email
        if (userDao.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        
        User user = new User(name, email, age);
        return userDao.create(user);
    }
    
    /**
     * Получает пользователя по ID.
     * 
     * @param id ID пользователя
     * @return Optional содержащий пользователя если найден
     */
    public Optional<User> getUserById(Long id) {
        logger.info("Getting user by ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + id);
        }
        return userDao.findById(id);
    }
    
    /**
     * Получает пользователя по email.
     * 
     * @param email email пользователя
     * @return Optional содержащий пользователя если найден
     */
    public Optional<User> getUserByEmail(String email) {
        logger.info("Getting user by email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userDao.findByEmail(email);
    }
    
    /**
     * Получает всех пользователей.
     * 
     * @return список всех пользователей
     */
    public List<User> getAllUsers() {
        logger.info("Getting all users");
        return userDao.findAll();
    }
    
    /**
     * Обновляет информацию о пользователе.
     * 
     * @param id ID пользователя
     * @param name новое имя (опционально)
     * @param email новый email (опционально)
     * @param age новый возраст (опционально)
     * @return обновленный пользователь
     * @throws IllegalArgumentException если пользователь не найден или валидация не прошла
     */
    public User updateUser(Long id, String name, String email, Integer age) {
        logger.info("Updating user with ID: {}, name: {}, email: {}, age: {}", id, name, email, age);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + id);
        }
        
        // Получаем существующего пользователя
        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        
        // Обновляем поля, если они предоставлены
        if (name != null && !name.trim().isEmpty()) {
            existingUser.setName(name.trim());
        }
        
        if (email != null && !email.trim().isEmpty()) {
            // Проверяем, отличается ли новый email и не занят ли он
            if (!email.equals(existingUser.getEmail()) && userDao.existsByEmail(email)) {
                throw new IllegalArgumentException("User with email " + email + " already exists");
            }
            existingUser.setEmail(email.trim());
        }
        
        if (age != null) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Age must be between 0 and 150");
            }
            existingUser.setAge(age);
        }
        
        return userDao.update(existingUser);
    }
    
    /**
     * Удаляет пользователя по ID.
     * 
     * @param id ID пользователя
     * @return true если пользователь был удален, false если не найден
     */
    public boolean deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + id);
        }
        
        return userDao.deleteById(id);
    }
    
    /**
     * Валидирует ввод пользователя.
     * 
     * @param name имя пользователя
     * @param email email пользователя
     * @param age возраст пользователя
     * @throws IllegalArgumentException если валидация не прошла
     */
    private void validateUserInput(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }
    
    /**
     * Простая валидация email.
     * 
     * @param email email для валидации
     * @return true если email валиден
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
