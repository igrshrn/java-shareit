package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(User user, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    Optional<Item> getItemById(long itemId);

    List<Item> getItemsByOwner(long userId);

    List<Item> searchItems(String text);
}
