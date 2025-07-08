package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item toItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .itemRequest(item.getRequest())
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .comments(item.getComments())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .authorName(comment.getUser().getName())
                .created(comment.getCreatedAt())
                .build();
    }
}
