package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(UserDto userDto) {
        userStorage.checkEmailUnique(userDto.getEmail());
        return userStorage.createUser(userDto);
    }

    @Override
    public User updateUser(long id, UserDto userDto) {
        userStorage.checkEmailUnique(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        return userStorage.updateUser(user);
    }

    @Override
    public User getUserById(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public void deleteUser(long id) {
        getUserById(id);
        userStorage.deleteUser(id);
    }
}
