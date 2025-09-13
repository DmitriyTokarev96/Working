# Примеры использования User Service

## Запуск приложения

После настройки базы данных и установки Maven, запустите приложение:

```bash
mvn exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"
```

## Примеры операций

### 1. Создание пользователя

```
=== Main Menu ===
1. Create User
2. Get User by ID
3. Get User by Email
4. Get All Users
5. Update User
6. Delete User
7. Search Users
0. Exit
Please enter your choice: 1

=== Create User ===
Enter name: John Doe
Enter email: john.doe@example.com
Enter age (optional, press Enter to skip): 30

User created successfully!
User details: User{id=1, name='John Doe', email='john.doe@example.com', age=30, createdAt=2024-01-15T10:30:00}
```

### 2. Поиск пользователя по ID

```
=== Main Menu ===
Please enter your choice: 2

=== Get User by ID ===
Enter user ID: 1

User found:
User{id=1, name='John Doe', email='john.doe@example.com', age=30, createdAt=2024-01-15T10:30:00}
```

### 3. Поиск пользователя по email

```
=== Main Menu ===
Please enter your choice: 3

=== Get User by Email ===
Enter email: john.doe@example.com

User found:
User{id=1, name='John Doe', email='john.doe@example.com', age=30, createdAt=2024-01-15T10:30:00}
```

### 4. Получение всех пользователей

```
=== Main Menu ===
Please enter your choice: 4

=== All Users ===
Found 3 user(s):
1. User{id=1, name='John Doe', email='john.doe@example.com', age=30, createdAt=2024-01-15T10:30:00}
2. User{id=2, name='Jane Smith', email='jane.smith@example.com', age=25, createdAt=2024-01-15T10:35:00}
3. User{id=3, name='Bob Johnson', email='bob.johnson@example.com', age=35, createdAt=2024-01-15T10:40:00}
```

### 5. Обновление пользователя

```
=== Main Menu ===
Please enter your choice: 5

=== Update User ===
Enter user ID to update: 1
Current user details: User{id=1, name='John Doe', email='john.doe@example.com', age=30, createdAt=2024-01-15T10:30:00}
Enter new values (press Enter to keep current value):
Name [John Doe]: John Updated
Email [john.doe@example.com]: john.updated@example.com
Age [30]: 31

User updated successfully!
Updated user details: User{id=1, name='John Updated', email='john.updated@example.com', age=31, createdAt=2024-01-15T10:30:00}
```

### 6. Удаление пользователя

```
=== Main Menu ===
Please enter your choice: 6

=== Delete User ===
Enter user ID to delete: 1
User to delete: User{id=1, name='John Updated', email='john.updated@example.com', age=31, createdAt=2024-01-15T10:30:00}
Are you sure you want to delete this user? (y/N): y

User deleted successfully!
```

### 7. Поиск пользователей

```
=== Main Menu ===
Please enter your choice: 7

=== Search Users ===
1. Search by name
2. Search by age range
Choose search option: 1

Enter name to search: John

Found 1 user(s):
1. User{id=1, name='John Doe', email='john.doe@example.com', age=30, createdAt=2024-01-15T10:30:00}
```

## Примеры ошибок

### Создание пользователя с существующим email

```
=== Create User ===
Enter name: Another User
Enter email: john.doe@example.com
Enter age (optional, press Enter to skip): 25

Error: User with email john.doe@example.com already exists
```

### Поиск несуществующего пользователя

```
=== Get User by ID ===
Enter user ID: 999

User not found with ID: 999
```

### Неверный формат возраста

```
=== Create User ===
Enter name: Test User
Enter email: test@example.com
Enter age (optional, press Enter to skip): abc

Error: Invalid age format. Please enter a valid number.
```

### Возраст вне допустимого диапазона

```
=== Create User ===
Enter name: Test User
Enter email: test@example.com
Enter age (optional, press Enter to skip): 200

Error: Age must be between 0 and 150
```

## Программное использование

Если вы хотите использовать UserService в своем коде:

```java
import com.example.userservice.service.UserService;
import com.example.userservice.entity.User;

public class ExampleUsage {
    public static void main(String[] args) {
        UserService userService = new UserService();
        
        try {
            // Создание пользователя
            User user = userService.createUser("John Doe", "john@example.com", 30);
            System.out.println("Created user: " + user);
            
            // Поиск пользователя
            Optional<User> foundUser = userService.getUserById(user.getId());
            if (foundUser.isPresent()) {
                System.out.println("Found user: " + foundUser.get());
            }
            
            // Обновление пользователя
            User updatedUser = userService.updateUser(user.getId(), "John Updated", null, 31);
            System.out.println("Updated user: " + updatedUser);
            
            // Удаление пользователя
            boolean deleted = userService.deleteUser(user.getId());
            System.out.println("User deleted: " + deleted);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

## Настройка логирования

Для изменения уровня логирования отредактируйте `src/main/resources/logback.xml`:

```xml
<!-- Изменить уровень логирования для приложения -->
<logger name="com.example.userservice" level="DEBUG" additivity="false">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
    <appender-ref ref="ERROR_FILE"/>
</logger>
```

## Мониторинг

Логи приложения сохраняются в:
- `logs/user-service.log` - все логи
- `logs/user-service-error.log` - только ошибки

Для мониторинга в реальном времени:
```bash
# Linux/macOS
tail -f logs/user-service.log

# Windows
Get-Content logs/user-service.log -Wait
```
