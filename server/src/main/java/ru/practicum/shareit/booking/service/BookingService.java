package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking createBooking(long userId, BookingCreateDto bookingCreateDto);

    Booking getBooking(long userId, long bookingId);

    Booking updateBooking(long userId, long bookingId, boolean approved);

    List<Booking> getBookingsByUser(long userId, BookingState state);

    List<Booking> getBookingsByOwner(long ownerId, BookingState state);

    Booking getLastBooking(long itemId);

    Booking getNextBooking(long itemId);

    boolean existsByBookerIdAndItemId(long bookerId, long itemId);
}
