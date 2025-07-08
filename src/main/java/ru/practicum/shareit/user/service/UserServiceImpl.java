package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserCreateDto userCreateDto) {
        this.existsByEmail(userCreateDto.getEmail());
        return userRepository.save(UserMapper.toUser(userCreateDto));
    }

    @Override
    public User updateUser(long id, UserUpdateDto userUpdateDto) {
        User existingUser = getUserById(id);

        if (userUpdateDto.getName() != null) {
            existingUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null) {
            this.existsByEmail(userUpdateDto.getEmail());
            existingUser.setEmail(userUpdateDto.getEmail());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public void deleteUser(long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("Пользователь с " + email + " уже существует");
        }
    }
}
