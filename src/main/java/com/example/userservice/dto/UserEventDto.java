package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEventDto {
    private String operation;
    private String email;
    private String username;

    @JsonCreator
    public UserEventDto(@JsonProperty("operation") String operation,
                       @JsonProperty("email") String email,
                       @JsonProperty("username") String username) {
        this.operation = operation;
        this.email = email;
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserEventDto{" +
                "operation='" + operation + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
