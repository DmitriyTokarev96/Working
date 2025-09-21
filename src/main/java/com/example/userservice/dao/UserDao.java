package com.example.userservice.dao;

import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Объект доступа к данным для сущности User.
 * Реализует CRUD операции для управления пользователями.
 */
public class UserDao {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final SessionFactory sessionFactory;
    
    public UserDao() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    /**
     * Создает нового пользователя в базе данных.
     * 
     * @param user пользователь для создания
     * @return созданный пользователь с сгенерированным ID
     * @throws RuntimeException если произошла ошибка при создании
     */
    public User create(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            logger.info("User created successfully: {}", user);
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error creating user: {}", user, e);
            throw new RuntimeException("Failed to create user", e);
        }
    }
    
    /**
     * Находит пользователя по ID.
     * 
     * @param id ID пользователя
     * @return Optional содержащий пользователя если найден, пустой иначе
     */
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                logger.info("User found by ID {}: {}", id, user);
            } else {
                logger.info("User not found with ID: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
            throw new RuntimeException("Failed to find user by ID", e);
        }
    }
    
    /**
     * Находит пользователя по email.
     * 
     * @param email email пользователя
     * @return Optional содержащий пользователя если найден, пустой иначе
     */
    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            if (user != null) {
                logger.info("User found by email {}: {}", email, user);
            } else {
                logger.info("User not found with email: {}", email);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by email: {}", email, e);
            throw new RuntimeException("Failed to find user by email", e);
        }
    }
    
    /**
     * Получает всех пользователей из базы данных.
     * 
     * @return список всех пользователей
     */
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            List<User> users = query.list();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }
    
    /**
     * Обновляет существующего пользователя одним запросом.
     * 
     * @param user пользователь для обновления
     * @return обновленный пользователь
     * @throws RuntimeException если пользователь не найден или обновление не удалось
     */
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            // Используем один запрос UPDATE вместо SELECT + UPDATE
            Query<Integer> updateQuery = session.createQuery(
                "UPDATE User SET name = :name, email = :email, age = :age WHERE id = :id"
            );
            updateQuery.setParameter("name", user.getName());
            updateQuery.setParameter("email", user.getEmail());
            updateQuery.setParameter("age", user.getAge());
            updateQuery.setParameter("id", user.getId());
            
            int updatedRows = updateQuery.executeUpdate();
            
            if (updatedRows == 0) {
                throw new RuntimeException("User not found with ID: " + user.getId());
            }
            
            transaction.commit();
            logger.info("User updated successfully in single query: {}", user);
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating user: {}", user, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }
    
    /**
     * Удаляет пользователя по ID.
     * 
     * @param id ID пользователя для удаления
     * @return true если пользователь был удален, false если не найден
     */
    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            User user = session.get(User.class, id);
            if (user == null) {
                logger.info("User not found with ID: {}", id);
                return false;
            }
            
            session.delete(user);
            transaction.commit();
            logger.info("User deleted successfully with ID: {}", id);
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting user with ID: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
    
    /**
     * Проверяет существование пользователя по email.
     * 
     * @param email email для проверки
     * @return true если пользователь существует, false иначе
     */
    public boolean existsByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM User WHERE email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.uniqueResult();
            boolean exists = count > 0;
            logger.info("User exists with email {}: {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking user existence by email: {}", email, e);
            throw new RuntimeException("Failed to check user existence", e);
        }
    }
}
