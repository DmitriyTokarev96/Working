package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * HATEOAS DTO для представления пользователя в API.
 * Расширяет RepresentationModel для поддержки HATEOAS ссылок.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User resource with HATEOAS links")
public class UserResourceDto extends RepresentationModel<UserResourceDto> {
    
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    
    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;
    
    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "Age of the user", example = "25", minimum = "0", maximum = "150")
    private Integer age;
    
    @Schema(description = "Date and time when the user was created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date and time when the user was last updated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * Конструктор для создания UserResourceDto из UserDto с ссылками
     */
    public UserResourceDto(UserDto userDto, List<Link> links) {
        this.id = userDto.getId();
        this.name = userDto.getName();
        this.email = userDto.getEmail();
        this.age = userDto.getAge();
        this.createdAt = userDto.getCreatedAt();
        this.updatedAt = userDto.getUpdatedAt();
        this.add(links);
    }
}
