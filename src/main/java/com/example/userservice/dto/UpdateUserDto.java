package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для обновления пользователя.
 * Все поля опциональны для частичного обновления.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for updating an existing user (all fields are optional)")
public class UpdateUserDto {
    
    @Schema(description = "Full name of the user", example = "John Doe Updated", minLength = 1, maxLength = 100)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;
    
    @Schema(description = "Email address of the user", example = "john.doe.updated@example.com")
    @Email(message = "Invalid email format")
    private String email;
    
    @Schema(description = "Age of the user", example = "26", minimum = "0", maximum = "150")
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 150, message = "Age must be at most 150")
    private Integer age;
}

