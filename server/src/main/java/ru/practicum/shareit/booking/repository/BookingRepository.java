package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByUserIdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);

    List<Booking> findByUserIdAndStartIsAfter(Long userId, LocalDateTime end, Sort sort);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByUserId(Long userId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    Booking findByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Booking findByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    boolean existsByUser_IdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);
}
