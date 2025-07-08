package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingState.PAST;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, CommentRepository commentRepository, @Lazy BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
    }

    @Override
    public Item createItem(long userId, ItemCreateDto itemCreateDto) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(user);

        return itemRepository.save(item);
    }

    @Override
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
    public Comment createComment(long userId, long itemId, CommentCreateDto commentCreateDto) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        List<Booking> bookings = bookingService.getBookingsByUser(userId, PAST);

        boolean hasBooked = bookings.stream().anyMatch(booking -> booking.getItem().getId().equals(itemId));
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
