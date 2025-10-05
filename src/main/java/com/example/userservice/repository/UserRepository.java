package com.example.userservice.repository;

import com.example.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository для работы с пользователями.
 * Предоставляет CRUD операции и кастомные запросы.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Находит пользователя по email.
     * 
     * @param email email пользователя
     * @return Optional содержащий пользователя если найден
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Проверяет существование пользователя по email.
     * 
     * @param email email для проверки
     * @return true если пользователь существует
     */
    boolean existsByEmail(String email);
    
    /**
     * Проверяет существование пользователя по email, исключая пользователя с указанным ID.
     * Используется при обновлении пользователя для проверки уникальности email.
     * 
     * @param email email для проверки
     * @param id ID пользователя для исключения
     * @return true если пользователь с таким email существует (кроме указанного ID)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}

