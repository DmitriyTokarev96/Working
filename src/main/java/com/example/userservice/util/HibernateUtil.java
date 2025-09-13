package com.example.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилитный класс для управления Hibernate SessionFactory.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра SessionFactory.
 */
public class HibernateUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;
    
    static {
        try {
            // Создаем экземпляр Configuration
            Configuration configuration = new Configuration();
            
            // Загружаем hibernate.cfg.xml из classpath
            configuration.configure("hibernate.cfg.xml");
            
            // Строим ServiceRegistry
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            
            // Строим SessionFactory
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            
            logger.info("Hibernate SessionFactory created successfully");
            
        } catch (Exception e) {
            logger.error("Error creating Hibernate SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }
    
    /**
     * Получает экземпляр SessionFactory.
     * 
     * @return экземпляр SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /**
     * Закрывает SessionFactory и освобождает ресурсы.
     */
    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}
