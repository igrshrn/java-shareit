package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User createUser(UserCreateDto userCreateDto);

    User updateUser(long id, UserUpdateDto userUpdateDto);

    User getUserById(long id);

    void deleteUser(long id);

    void existsByEmail(String email);
}
