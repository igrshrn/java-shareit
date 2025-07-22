package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }


    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @PathVariable @Positive Long itemId,
                                                @RequestBody CommentCreateDto commentCreateDto) {
        return itemClient.createComment(userId, itemId, commentCreateDto);
    }

}
