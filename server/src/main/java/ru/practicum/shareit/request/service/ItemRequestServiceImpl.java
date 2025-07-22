package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserService userService, ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemRequest createItemRequest(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = userService.getUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(itemRequestCreateDto);
        itemRequest.setUser(user);

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getUserRequests(long userId) {
        userService.getUserById(userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<ItemRequest> getAllRequests(long userId) {
        userService.getUserById(userId);
        return itemRequestRepository.findAllOtherUsersRequests(userId);
    }

    @Override
    public ItemRequest getRequestById(long userId, long requestId) {
        userService.getUserById(userId);
        return itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID = " + requestId + " не найден"));
    }
}
