package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для UserService с использованием Mockito.
 * Тесты изолированы и не требуют реальной базы данных.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceUnitTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private final String TEST_NAME = "Иван Петров";
    private final String TEST_EMAIL = "ivan.petrov@example.com";
    private final Integer TEST_AGE = 30;
    private final Long TEST_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
        testUser.setId(TEST_ID);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Создание пользователей")
    class CreateUserTests {

        @Test
        @DisplayName("Должен создать пользователя с валидными данными")
        void shouldCreateUserWithValidData() {
            // Given
            when(userDao.existsByEmail(TEST_EMAIL)).thenReturn(false);
            when(userDao.create(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.createUser(TEST_NAME, TEST_EMAIL, TEST_AGE);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(TEST_NAME);
            assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
            assertThat(result.getAge()).isEqualTo(TEST_AGE);

            verify(userDao).existsByEmail(TEST_EMAIL);
            verify(userDao).create(any(User.class));
        }

        @Test
        @DisplayName("Должен создать пользователя с null возрастом")
        void shouldCreateUserWithNullAge() {
            // Given
            User userWithNullAge = new User(TEST_NAME, TEST_EMAIL, null);
            userWithNullAge.setId(TEST_ID);
            userWithNullAge.setCreatedAt(LocalDateTime.now());

            when(userDao.existsByEmail(TEST_EMAIL)).thenReturn(false);
            when(userDao.create(any(User.class))).thenReturn(userWithNullAge);

            // When
            User result = userService.createUser(TEST_NAME, TEST_EMAIL, null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getAge()).isNull();
        }

        @Test
        @DisplayName("Должен выбросить исключение при создании пользователя с существующим email")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Given
            when(userDao.existsByEmail(TEST_EMAIL)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(TEST_NAME, TEST_EMAIL, TEST_AGE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User with email " + TEST_EMAIL + " already exists");

            verify(userDao).existsByEmail(TEST_EMAIL);
            verify(userDao, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при null имени")
        void shouldThrowExceptionWhenNameIsNull() {
            // When & Then
            assertThatThrownBy(() -> userService.createUser(null, TEST_EMAIL, TEST_AGE))
                    .isInstanceOf(NullPointerException.class);

            verify(userDao, never()).existsByEmail(any());
            verify(userDao, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при null email")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> userService.createUser(TEST_NAME, null, TEST_AGE))
                    .isInstanceOf(NullPointerException.class);

            verify(userDao, never()).existsByEmail(any());
            verify(userDao, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при пустом имени")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // When & Then
            assertThatThrownBy(() -> userService.createUser("", TEST_EMAIL, TEST_AGE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name cannot be empty");

            verify(userDao, never()).existsByEmail(any());
            verify(userDao, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при невалидном email")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            // When & Then
            assertThatThrownBy(() -> userService.createUser(TEST_NAME, "invalid-email", TEST_AGE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid email format: invalid-email");

            verify(userDao, never()).existsByEmail(any());
            verify(userDao, never()).create(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при невалидном возрасте")
        void shouldThrowExceptionWhenAgeIsInvalid() {
            // When & Then
            assertThatThrownBy(() -> userService.createUser(TEST_NAME, TEST_EMAIL, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Age must be between 0 and 150");

            verify(userDao, never()).existsByEmail(any());
            verify(userDao, never()).create(any(User.class));
        }
    }

    @Nested
    @DisplayName("Получение пользователей")
    class GetUserTests {

        @Test
        @DisplayName("Должен получить пользователя по ID")
        void shouldGetUserById() {
            // Given
            when(userDao.findById(TEST_ID)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserById(TEST_ID);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userDao).findById(TEST_ID);
        }

        @Test
        @DisplayName("Должен вернуть пустой Optional для несуществующего ID")
        void shouldReturnEmptyOptionalForNonExistentId() {
            // Given
            when(userDao.findById(TEST_ID)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.getUserById(TEST_ID);

            // Then
            assertThat(result).isEmpty();
            verify(userDao).findById(TEST_ID);
        }

        @Test
        @DisplayName("Должен выбросить исключение при невалидном ID")
        void shouldThrowExceptionForInvalidId() {
            // When & Then
            assertThatThrownBy(() -> userService.getUserById(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID: -1");

            verify(userDao, never()).findById(any());
        }

        @Test
        @DisplayName("Должен получить пользователя по email")
        void shouldGetUserByEmail() {
            // Given
            when(userDao.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserByEmail(TEST_EMAIL);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userDao).findByEmail(TEST_EMAIL);
        }

        @Test
        @DisplayName("Должен выбросить исключение при null email")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> userService.getUserByEmail(null))
                    .isInstanceOf(NullPointerException.class);

            verify(userDao, never()).findByEmail(any());
        }

        @Test
        @DisplayName("Должен получить всех пользователей")
        void shouldGetAllUsers() {
            // Given
            List<User> users = Arrays.asList(testUser, new User("Пользователь 2", "user2@example.com", 25));
            when(userDao.findAll()).thenReturn(users);

            // When
            List<User> result = userService.getAllUsers();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(users);
            verify(userDao).findAll();
        }

        @Test
        @DisplayName("Должен вернуть пустой список когда пользователей нет")
        void shouldReturnEmptyListWhenNoUsers() {
            // Given
            when(userDao.findAll()).thenReturn(Collections.emptyList());

            // When
            List<User> result = userService.getAllUsers();

            // Then
            assertThat(result).isEmpty();
            verify(userDao).findAll();
        }
    }

    @Nested
    @DisplayName("Обновление пользователей")
    class UpdateUserTests {

        @Test
        @DisplayName("Должен обновить пользователя со всеми полями")
        void shouldUpdateUserWithAllFields() {
            // Given
            User updatedUser = new User("Обновленное имя", "updated@example.com", 35);
            updatedUser.setId(TEST_ID);
            updatedUser.setCreatedAt(testUser.getCreatedAt());

            when(userDao.findById(TEST_ID)).thenReturn(Optional.of(testUser));
            when(userDao.existsByEmail("updated@example.com")).thenReturn(false);
            when(userDao.update(any(User.class))).thenReturn(updatedUser);

            // When
            User result = userService.updateUser(TEST_ID, "Обновленное имя", "updated@example.com", 35);

            // Then
            assertThat(result.getName()).isEqualTo("Обновленное имя");
            assertThat(result.getEmail()).isEqualTo("updated@example.com");
            assertThat(result.getAge()).isEqualTo(35);

            verify(userDao).findById(TEST_ID);
            verify(userDao).existsByEmail("updated@example.com");
            verify(userDao).update(any(User.class));
        }

        @Test
        @DisplayName("Должен обновить только имя пользователя")
        void shouldUpdateOnlyName() {
            // Given
            when(userDao.findById(TEST_ID)).thenReturn(Optional.of(testUser));
            when(userDao.update(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.updateUser(TEST_ID, "Новое имя", null, null);

            // Then
            verify(userDao).findById(TEST_ID);
            verify(userDao).update(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при обновлении несуществующего пользователя")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // Given
            when(userDao.findById(TEST_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(TEST_ID, "Новое имя", null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User not found with ID: " + TEST_ID);

            verify(userDao).findById(TEST_ID);
            verify(userDao, never()).update(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при обновлении с занятым email")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Given
            when(userDao.findById(TEST_ID)).thenReturn(Optional.of(testUser));
            when(userDao.existsByEmail("existing@example.com")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(TEST_ID, null, "existing@example.com", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User with email existing@example.com already exists");

            verify(userDao).findById(TEST_ID);
            verify(userDao).existsByEmail("existing@example.com");
            verify(userDao, never()).update(any(User.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение при невалидном ID")
        void shouldThrowExceptionForInvalidId() {
            // When & Then
            assertThatThrownBy(() -> userService.updateUser(-1L, "Новое имя", null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID: -1");

            verify(userDao, never()).findById(any());
            verify(userDao, never()).update(any(User.class));
        }
    }

    @Nested
    @DisplayName("Удаление пользователей")
    class DeleteUserTests {

        @Test
        @DisplayName("Должен удалить пользователя")
        void shouldDeleteUser() {
            // Given
            when(userDao.deleteById(TEST_ID)).thenReturn(true);

            // When
            boolean result = userService.deleteUser(TEST_ID);

            // Then
            assertThat(result).isTrue();
            verify(userDao).deleteById(TEST_ID);
        }

        @Test
        @DisplayName("Должен вернуть false при удалении несуществующего пользователя")
        void shouldReturnFalseWhenDeletingNonExistentUser() {
            // Given
            when(userDao.deleteById(TEST_ID)).thenReturn(false);

            // When
            boolean result = userService.deleteUser(TEST_ID);

            // Then
            assertThat(result).isFalse();
            verify(userDao).deleteById(TEST_ID);
        }

        @Test
        @DisplayName("Должен выбросить исключение при невалидном ID")
        void shouldThrowExceptionForInvalidId() {
            // When & Then
            assertThatThrownBy(() -> userService.deleteUser(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid user ID: -1");

            verify(userDao, never()).deleteById(any());
        }
    }
}



