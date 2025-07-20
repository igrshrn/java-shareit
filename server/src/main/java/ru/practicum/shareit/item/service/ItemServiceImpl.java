package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, CommentRepository commentRepository, @Lazy BookingService bookingService, ItemRequestService itemRequestService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
        this.itemRequestService = itemRequestService;
    }

    @Override
    @Transactional
    public Item createItem(long userId, ItemCreateDto itemCreateDto) {
        User user = userService.getUserById(userId);

        Item item = ItemMapper.INSTANCE.toItem(itemCreateDto);
        item.setOwner(user);

        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        userService.getUserById(userId);
        Item item = getItemById(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text);
    }

    @Override
    @Transactional
    public Comment createComment(long userId, long itemId, CommentCreateDto commentCreateDto) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        boolean hasBooked = bookingService.existsByBookerIdAndItemId(userId, itemId);

        if (!hasBooked) {
            throw new WrongException("Пользователь с id " + userId + " не брал вещь с itemId " + itemId + " в аренду");
        }
        Comment comment = Comment.builder()
                .text(commentCreateDto.getText())
                .item(item)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

}
