package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(1);

    @Override
    public User createUser(UserDto userDto) {
        long id = currentId.getAndIncrement();
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        users.put(id, user);
        log.info("Создан пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User updatedUser = users.get(user.getId());
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        users.put(user.getId(), updatedUser);
        log.info("Пользователь с id:{} обновлен updatedUser: {}", user.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
        log.info("Пользователь с id:{} удален", id);
    }

    @Override
    public void checkEmailUnique(String email) {
        boolean emailExists = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (emailExists) {
            throw new AlreadyExistsException("Email уже существует: " + email);
        }
    }
}
