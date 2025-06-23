package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserStorage {
    User createUser(UserDto userDto);

    User updateUser(User user);

    Optional<User> getUserById(long id);

    void deleteUser(long id);

    void checkEmailUnique(String email);
}
