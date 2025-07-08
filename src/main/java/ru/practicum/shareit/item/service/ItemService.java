package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(long userId, ItemCreateDto itemCreateDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    Item getItemById(long itemId);

    List<Item> getItemsByOwner(long userId);

    List<Item> searchItems(String text);

    Comment createComment(long userId, long itemId, CommentCreateDto commentCreateDto);
}
