package com.example.userservice;

import com.example.userservice.console.UserConsole;
import com.example.userservice.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс приложения User Service.
 * Инициализирует Hibernate и запускает консольный интерфейс.
 */
public class UserServiceApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting User Service Application");
        
        try {
            // Инициализируем Hibernate
            HibernateUtil.getSessionFactory();
            logger.info("Hibernate initialized successfully");
            
            // Запускаем консольный интерфейс
            UserConsole console = new UserConsole();
            console.start();
            
        } catch (Exception e) {
            logger.error("Failed to start User Service Application", e);
            System.err.println("Failed to start application: " + e.getMessage());
            System.exit(1);
        } finally {
            // Очищаем ресурсы
            try {
                HibernateUtil.closeSessionFactory();
                logger.info("Application shutdown completed");
            } catch (Exception e) {
                logger.error("Error during application shutdown", e);
            }
        }
    }
}
