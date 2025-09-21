package com.example.userservice;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Базовый класс для тестов с общими настройками изоляции.
 * Обеспечивает изоляцию тестов друг от друга.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    /**
     * Настройка изоляции для каждого тестового класса.
     * Каждый тестовый класс выполняется в отдельном контексте.
     */
    protected void setUpIsolation() {
        // Очистка системных свойств для изоляции
        System.clearProperty("hibernate.connection.url");
        System.clearProperty("hibernate.connection.username");
        System.clearProperty("hibernate.connection.password");
        System.clearProperty("hibernate.connection.driver_class");
        System.clearProperty("hibernate.dialect");
        System.clearProperty("hibernate.hbm2ddl.auto");
    }

    /**
     * Очистка после тестов для обеспечения изоляции.
     */
    protected void tearDownIsolation() {
        // Очистка системных свойств
        setUpIsolation();
        
        // Дополнительная очистка при необходимости
        System.gc(); // Принудительная сборка мусора
    }
}
