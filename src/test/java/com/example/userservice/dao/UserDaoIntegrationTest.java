package com.example.userservice.dao;

import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционные тесты для UserDao с использованием Testcontainers.
 * Каждый тест изолирован и использует отдельную базу данных.
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private UserDao userDao;
    private SessionFactory sessionFactory;

    @BeforeAll
    static void setupContainer() {
        postgresContainer.start();
    }

    @AfterAll
    static void tearDownContainer() {
        postgresContainer.stop();
    }

    @BeforeEach
    void setUp() {
        // Настройка Hibernate для использования Testcontainers
        System.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgresContainer.getUsername());
        System.setProperty("hibernate.connection.password", postgresContainer.getPassword());
        System.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        System.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        // Переинициализация Hibernate с новыми настройками
        HibernateUtil.reset();
        sessionFactory = HibernateUtil.getSessionFactory();
        userDao = new UserDao();
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        // Очистка системных свойств
        System.clearProperty("hibernate.connection.url");
        System.clearProperty("hibernate.connection.username");
        System.clearProperty("hibernate.connection.password");
        System.clearProperty("hibernate.connection.driver_class");
        System.clearProperty("hibernate.dialect");
        System.clearProperty("hibernate.hbm2ddl.auto");
    }

    @Test
    @Order(1)
    @DisplayName("Создание пользователя должно сохранить его в базе данных")
    void testCreateUser() {
        // Given
        User user = new User("Иван Петров", "ivan.petrov@example.com", 30);

        // When
        User savedUser = userDao.create(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Иван Петров");
        assertThat(savedUser.getEmail()).isEqualTo("ivan.petrov@example.com");
        assertThat(savedUser.getAge()).isEqualTo(30);
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Поиск пользователя по ID должен вернуть правильного пользователя")
    void testFindById() {
        // Given
        User user = new User("Мария Сидорова", "maria.sidorova@example.com", 25);
        User savedUser = userDao.create(user);

        // When
        Optional<User> foundUser = userDao.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo("Мария Сидорова");
        assertThat(foundUser.get().getEmail()).isEqualTo("maria.sidorova@example.com");
    }

    @Test
    @Order(3)
    @DisplayName("Поиск пользователя по несуществующему ID должен вернуть пустой Optional")
    void testFindByIdNotFound() {
        // When
        Optional<User> foundUser = userDao.findById(999L);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Поиск пользователя по email должен вернуть правильного пользователя")
    void testFindByEmail() {
        // Given
        User user = new User("Алексей Козлов", "alexey.kozlov@example.com", 35);
        userDao.create(user);

        // When
        Optional<User> foundUser = userDao.findByEmail("alexey.kozlov@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Алексей Козлов");
        assertThat(foundUser.get().getEmail()).isEqualTo("alexey.kozlov@example.com");
    }

    @Test
    @Order(5)
    @DisplayName("Поиск пользователя по несуществующему email должен вернуть пустой Optional")
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userDao.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @Order(6)
    @DisplayName("Получение всех пользователей должно вернуть всех сохраненных пользователей")
    void testFindAll() {
        // Given
        userDao.create(new User("Пользователь 1", "user1@example.com", 20));
        userDao.create(new User("Пользователь 2", "user2@example.com", 25));
        userDao.create(new User("Пользователь 3", "user3@example.com", 30));

        // When
        List<User> users = userDao.findAll();

        // Then
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("Пользователь 1", "Пользователь 2", "Пользователь 3");
    }

    @Test
    @Order(7)
    @DisplayName("Обновление пользователя должно изменить его данные в базе")
    void testUpdate() {
        // Given
        User user = new User("Оригинальное имя", "original@example.com", 25);
        User savedUser = userDao.create(user);
        savedUser.setName("Обновленное имя");
        savedUser.setEmail("updated@example.com");
        savedUser.setAge(30);

        // When
        User updatedUser = userDao.update(savedUser);

        // Then
        assertThat(updatedUser.getName()).isEqualTo("Обновленное имя");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getAge()).isEqualTo(30);

        // Проверяем, что изменения сохранились в базе
        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Обновленное имя");
    }

    @Test
    @Order(8)
    @DisplayName("Удаление пользователя по ID должно удалить его из базы")
    void testDeleteById() {
        // Given
        User user = new User("Для удаления", "delete@example.com", 40);
        User savedUser = userDao.create(user);

        // When
        boolean deleted = userDao.deleteById(savedUser.getId());

        // Then
        assertThat(deleted).isTrue();
        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    @Order(9)
    @DisplayName("Удаление несуществующего пользователя должно вернуть false")
    void testDeleteByIdNotFound() {
        // When
        boolean deleted = userDao.deleteById(999L);

        // Then
        assertThat(deleted).isFalse();
    }

    @Test
    @Order(10)
    @DisplayName("Проверка существования пользователя по email должна работать корректно")
    void testExistsByEmail() {
        // Given
        userDao.create(new User("Существующий", "existing@example.com", 25));

        // When & Then
        assertThat(userDao.existsByEmail("existing@example.com")).isTrue();
        assertThat(userDao.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @Order(11)
    @DisplayName("Тест транзакционности - откат при ошибке")
    void testTransactionRollback() {
        // Given
        User validUser = new User("Валидный пользователь", "valid@example.com", 25);
        
        // When - создаем валидного пользователя
        User savedUser = userDao.create(validUser);
        assertThat(savedUser).isNotNull();

        // When - пытаемся обновить с несуществующим ID (должно вызвать исключение)
        User invalidUser = new User("Невалидный", "invalid@example.com", 30);
        invalidUser.setId(999L); // Несуществующий ID

        // Then
        assertThatThrownBy(() -> userDao.update(invalidUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 999");

        // Проверяем, что валидный пользователь остался нетронутым
        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Валидный пользователь");
    }

    @Test
    @Order(12)
    @DisplayName("Тест работы с null значениями")
    void testNullValues() {
        // Given
        User userWithNullAge = new User("Без возраста", "noage@example.com", null);

        // When
        User savedUser = userDao.create(userWithNullAge);

        // Then
        assertThat(savedUser.getAge()).isNull();
        
        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getAge()).isNull();
    }
}
