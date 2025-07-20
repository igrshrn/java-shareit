package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }


    @PostMapping
    public ItemRequestDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequestService.createItemRequest(userId, itemRequestCreateDto));
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService
                .getUserRequests(userId)
                .stream()
                .map(ItemRequestMapper.INSTANCE::toItemRequestDto)
                .toList();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService
                .getAllRequests(userId)
                .stream()
                .map(ItemRequestMapper.INSTANCE::toItemRequestDto)
                .toList();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequestService.getRequestById(userId, requestId));
    }
}
