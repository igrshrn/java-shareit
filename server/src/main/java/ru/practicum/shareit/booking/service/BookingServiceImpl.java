package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Booking createBooking(long userId, BookingCreateDto bookingCreateDto) {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingCreateDto.getItemId());

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна для бронирования");
        }

        Booking booking = BookingMapper.INSTANCE.toBooking(bookingCreateDto);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStatus(WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (booking.getUser().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new WrongException("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
        }
        return booking;
    }

    @Override
    @Transactional
    public Booking updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(userId, bookingId);
        if (userId != booking.getItem().getOwner().getId()) {
            throw new WrongException(
                    "Пользователь с id " + userId + " не может подверждать/отклонять бронирование. Так как не является владельцем вещи " + booking.getItem().getOwner().getId());
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByUser(long userId, BookingState state) {
        userService.getUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookingRepository.findByUserIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
            case PAST -> bookingRepository.findByUserIdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookingRepository.findByUserIdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookingRepository.findByUserIdAndStatus(userId, WAITING, sort);
            case REJECTED -> bookingRepository.findByUserIdAndStatus(userId, REJECTED, sort);
            default -> bookingRepository.findByUserId(userId, sort);
        };
    }

    @Override
    public List<Booking> getBookingsByOwner(long ownerId, BookingState state) {
        userService.getUserById(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, sort);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, now, sort);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, now, sort);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, WAITING, sort);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, REJECTED, sort);
            default -> bookingRepository.findByItemOwnerId(ownerId, sort);
        };
    }

    @Override
    public Booking getLastBooking(long itemId) {
        return bookingRepository.findByItemIdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now());
    }

    @Override
    public Booking getNextBooking(long itemId) {
        return bookingRepository.findByItemIdAndStartIsAfterOrderByStartAsc(itemId, LocalDateTime.now());
    }

    @Override
    public boolean existsByBookerIdAndItemId(long bookerId, long itemId) {
        return bookingRepository.existsByUser_IdAndItemIdAndEndBefore(bookerId, itemId, LocalDateTime.now());
    }


}
