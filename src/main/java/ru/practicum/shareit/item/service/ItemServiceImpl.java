package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Item createItem(long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        return itemStorage.createItem(user, itemDto);
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.getUserById(userId);
        getItemById(itemId);
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + itemId + " не найден"));
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        return itemStorage.getItemsByOwner(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemStorage.searchItems(text);
    }

}
