package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(long userId, ItemRequestCreateDto requestDto);

    List<ItemRequest> getUserRequests(long userId);

    List<ItemRequest> getAllRequests(long userId);

    ItemRequest getRequestById(long userId, long requestId);
}
