package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    User createUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    User getUserById(long id);

    void deleteUser(long id);
}
