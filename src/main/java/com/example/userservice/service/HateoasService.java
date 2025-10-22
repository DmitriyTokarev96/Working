package com.example.userservice.service;

import com.example.userservice.controller.UserController;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserResourceDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для создания HATEOAS ссылок для пользователей.
 */
@Service
public class HateoasService {

    /**
     * Создает UserResourceDto с HATEOAS ссылками из UserDto.
     */
    public UserResourceDto toUserResource(UserDto userDto) {
        List<Link> links = createUserLinks(userDto.getId());
        return new UserResourceDto(userDto, links);
    }

    /**
     * Создает список HATEOAS ссылок для пользователя.
     */
    private List<Link> createUserLinks(Long userId) {
        List<Link> links = new ArrayList<>();
        
        try {
            // Ссылка на самого себя
            Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUserById(userId)
            ).withSelfRel();
            links.add(selfLink);
            
            // Ссылка на обновление
            Link updateLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).updateUser(userId, null)
            ).withRel("update");
            links.add(updateLink);
            
            // Ссылка на удаление
            Link deleteLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).deleteUser(userId)
            ).withRel("delete");
            links.add(deleteLink);
            
            // Ссылка на всех пользователей
            Link allUsersLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()
            ).withRel("all-users");
            links.add(allUsersLink);
            
        } catch (Exception e) {
            // В случае ошибки возвращаем пустой список ссылок
            // Это может произойти при создании ссылок на методы контроллера
        }
        
        return links;
    }
}
