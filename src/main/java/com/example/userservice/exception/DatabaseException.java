package com.example.userservice.exception;

/**
 * Исключение для операций, связанных с базой данных.
 * Используется для обертывания специфичных для базы данных исключений и предоставления осмысленных сообщений об ошибках.
 */
public class DatabaseException extends UserServiceException {
    
    public DatabaseException(String message) {
        super("DATABASE_ERROR", message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, cause);
    }
}
