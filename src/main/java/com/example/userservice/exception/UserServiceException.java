package com.example.userservice.exception;

/**
 * Пользовательское исключение для операций User Service.
 * Используется для обертывания и предоставления осмысленных сообщений об ошибках для операций с пользователями.
 */
public class UserServiceException extends Exception {
    
    private final String errorCode;
    
    public UserServiceException(String message) {
        super(message);
        this.errorCode = "USER_SERVICE_ERROR";
    }
    
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "USER_SERVICE_ERROR";
    }
    
    public UserServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public UserServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
