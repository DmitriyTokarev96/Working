package com.example.userservice;

import com.example.userservice.dao.UserDao;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.TestInstance;

/**
 * Базовый класс для тестов с общими настройками изоляции.
 * Обеспечивает изоляцию тестов друг от друга.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    /**
     * Конструктор по умолчанию.
     */
    protected BaseTest() {
        // Пустой конструктор
    }

    /**
     * Настройка изоляции для каждого тестового класса.
     * Очищает записи в таблице User перед каждым тестом.
     */
    protected void setUpIsolation() {
        clearUserTable();
    }

    /**
     * Очистка после тестов для обеспечения изоляции.
     * Очищает записи в таблице User после тестов.
     */
    protected void tearDownIsolation() {
        clearUserTable();
    }
    
    /**
     * Очищает все записи из таблицы User.
     * Используется для изоляции тестов.
     */
    private void clearUserTable() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Transaction transaction = null;
        
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            // Удаляем все записи из таблицы User
            Query<?> deleteQuery = session.createQuery("DELETE FROM User");
            int deletedCount = deleteQuery.executeUpdate();
            
            transaction.commit();
            
            if (deletedCount > 0) {
                System.out.println("Cleared " + deletedCount + " users from database");
            }
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error clearing user table: " + e.getMessage());
            // Не выбрасываем исключение, чтобы не нарушить выполнение тестов
        }
    }
}



