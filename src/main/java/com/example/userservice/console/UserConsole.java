package com.example.userservice.console;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Консольный интерфейс для операций управления пользователями.
 * Предоставляет интерактивное меню для CRUD операций.
 */
public class UserConsole {
    
    private static final Logger logger = LoggerFactory.getLogger(UserConsole.class);
    private final UserService userService;
    private final Scanner scanner;
    
    public UserConsole() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Запускает консольное приложение.
     */
    public void start() {
        logger.info("Starting User Service Console Application");
        
        System.out.println("=== User Service Console Application ===");
        System.out.println("Welcome to the User Management System!");
        
        boolean running = true;
        while (running) {
            try {
                displayMenu();
                int choice = getChoice();
                
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        getUserById();
                        break;
                    case 3:
                        getUserByEmail();
                        break;
                    case 4:
                        getAllUsers();
                        break;
                    case 5:
                        updateUser();
                        break;
                    case 6:
                        deleteUser();
                        break;
                    case 7:
                        searchUsers();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Thank you for using User Service! Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                
                if (running) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
                
            } catch (Exception e) {
                logger.error("Error in console application", e);
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        
        scanner.close();
        logger.info("User Service Console Application stopped");
    }
    
    /**
     * Отображает главное меню.
     */
    private void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Create User");
        System.out.println("2. Get User by ID");
        System.out.println("3. Get User by Email");
        System.out.println("4. Get All Users");
        System.out.println("5. Update User");
        System.out.println("6. Delete User");
        System.out.println("7. Search Users");
        System.out.println("0. Exit");
        System.out.print("Please enter your choice: ");
    }
    
    /**
     * Получает выбор пользователя из ввода.
     * 
     * @return выбор как целое число
     */
    private int getChoice() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Please enter a choice: ");
                    continue;
                }
                int choice = Integer.parseInt(input);
                if (choice < 0 || choice > 7) {
                    System.out.print("Please enter a number between 0 and 7: ");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    /**
     * Создает нового пользователя.
     */
    private void createUser() {
        System.out.println("\n=== Create User ===");
        
        try {
            String name = getValidatedInput("Enter name: ", "Name cannot be empty", 
                input -> !input.trim().isEmpty());
            
            String email = getValidatedInput("Enter email: ", "Email cannot be empty", 
                input -> !input.trim().isEmpty());
            
            System.out.print("Enter age (optional, press Enter to skip): ");
            String ageInput = scanner.nextLine().trim();
            Integer age = null;
            if (!ageInput.isEmpty()) {
                try {
                    age = Integer.parseInt(ageInput);
                    if (age < 0 || age > 150) {
                        System.out.println("Error: Age must be between 0 and 150");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid age format. Please enter a valid number.");
                    return;
                }
            }
            
            User user = userService.createUser(name, email, age);
            System.out.println("User created successfully!");
            System.out.println("User details: " + user);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Получает пользователя по ID.
     */
    private void getUserById() {
        System.out.println("\n=== Get User by ID ===");
        
        try {
            System.out.print("Enter user ID: ");
            String input = scanner.nextLine().trim();
            Long id = Long.parseLong(input);
            
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                System.out.println("User found:");
                System.out.println(user.get());
            } else {
                System.out.println("User not found with ID: " + id);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID format. Please enter a valid number.");
        }
    }
    
    /**
     * Получает пользователя по email.
     */
    private void getUserByEmail() {
        System.out.println("\n=== Get User by Email ===");
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            System.out.println("User found:");
            System.out.println(user.get());
        } else {
            System.out.println("User not found with email: " + email);
        }
    }
    
    /**
     * Получает всех пользователей.
     */
    private void getAllUsers() {
        System.out.println("\n=== All Users ===");
        
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("Found " + users.size() + " user(s):");
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i));
            }
        }
    }
    
    /**
     * Обновляет пользователя.
     */
    private void updateUser() {
        System.out.println("\n=== Update User ===");
        
        try {
            System.out.print("Enter user ID to update: ");
            String input = scanner.nextLine().trim();
            Long id = Long.parseLong(input);
            
            // Check if user exists
            Optional<User> existingUser = userService.getUserById(id);
            if (!existingUser.isPresent()) {
                System.out.println("User not found with ID: " + id);
                return;
            }
            
            System.out.println("Current user details: " + existingUser.get());
            System.out.println("Enter new values (press Enter to keep current value):");
            
            System.out.print("Name [" + existingUser.get().getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                name = null;
            }
            
            System.out.print("Email [" + existingUser.get().getEmail() + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                email = null;
            }
            
            System.out.print("Age [" + existingUser.get().getAge() + "]: ");
            String ageInput = scanner.nextLine().trim();
            Integer age = null;
            if (!ageInput.isEmpty()) {
                age = Integer.parseInt(ageInput);
            }
            
            User updatedUser = userService.updateUser(id, name, email, age);
            System.out.println("User updated successfully!");
            System.out.println("Updated user details: " + updatedUser);
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID or age format. Please enter valid numbers.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Удаляет пользователя.
     */
    private void deleteUser() {
        System.out.println("\n=== Delete User ===");
        
        try {
            System.out.print("Enter user ID to delete: ");
            String input = scanner.nextLine().trim();
            Long id = Long.parseLong(input);
            
            // Check if user exists first
            Optional<User> user = userService.getUserById(id);
            if (!user.isPresent()) {
                System.out.println("User not found with ID: " + id);
                return;
            }
            
            System.out.println("User to delete: " + user.get());
            System.out.print("Are you sure you want to delete this user? (y/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("y") || confirmation.equals("yes")) {
                boolean deleted = userService.deleteUser(id);
                if (deleted) {
                    System.out.println("User deleted successfully!");
                } else {
                    System.out.println("Failed to delete user.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID format. Please enter a valid number.");
        }
    }
    
    /**
     * Поиск пользователей (простая реализация).
     */
    private void searchUsers() {
        System.out.println("\n=== Search Users ===");
        System.out.println("1. Search by name");
        System.out.println("2. Search by age range");
        System.out.print("Choose search option: ");
        
        try {
            int searchChoice = getChoice();
            
            switch (searchChoice) {
                case 1:
                    searchByName();
                    break;
                case 2:
                    searchByAgeRange();
                    break;
                default:
                    System.out.println("Invalid search option.");
            }
            
        } catch (Exception e) {
            System.out.println("Error during search: " + e.getMessage());
        }
    }
    
    /**
     * Поиск пользователей по имени.
     */
    private void searchByName() {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();
        
        List<User> allUsers = userService.getAllUsers();
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        
        if (filteredUsers.isEmpty()) {
            System.out.println("No users found with name containing: " + name);
        } else {
            System.out.println("Found " + filteredUsers.size() + " user(s):");
            for (int i = 0; i < filteredUsers.size(); i++) {
                System.out.println((i + 1) + ". " + filteredUsers.get(i));
            }
        }
    }
    
    /**
     * Поиск пользователей по диапазону возраста.
     */
    private void searchByAgeRange() {
        try {
            System.out.print("Enter minimum age: ");
            String minAgeInput = scanner.nextLine().trim();
            int minAge = Integer.parseInt(minAgeInput);
            
            System.out.print("Enter maximum age: ");
            String maxAgeInput = scanner.nextLine().trim();
            int maxAge = Integer.parseInt(maxAgeInput);
            
            if (minAge > maxAge) {
                System.out.println("Error: Minimum age cannot be greater than maximum age.");
                return;
            }
            
            List<User> allUsers = userService.getAllUsers();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getAge() != null && 
                            user.getAge() >= minAge && user.getAge() <= maxAge)
                    .toList();
            
            if (filteredUsers.isEmpty()) {
                System.out.println("No users found in age range " + minAge + "-" + maxAge);
            } else {
                System.out.println("Found " + filteredUsers.size() + " user(s) in age range " + minAge + "-" + maxAge + ":");
                for (int i = 0; i < filteredUsers.size(); i++) {
                    System.out.println((i + 1) + ". " + filteredUsers.get(i));
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid age format. Please enter valid numbers.");
        }
    }
    
    /**
     * Получает валидированный ввод от пользователя.
     * 
     * @param prompt сообщение-подсказка
     * @param errorMessage сообщение об ошибке для неверного ввода
     * @param validator предикат валидации
     * @return валидированная строка ввода
     */
    private String getValidatedInput(String prompt, String errorMessage, Predicate<String> validator) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println("Error: " + errorMessage);
        }
    }
}
