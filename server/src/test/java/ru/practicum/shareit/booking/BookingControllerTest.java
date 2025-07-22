package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.AbstractControllerTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RandomUtils;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.utils.HttpMethodEnum.*;

class BookingControllerTest extends AbstractControllerTest {

    @Test
    void createBooking() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings/" + booking.getId(), bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()))
                .andExpect(jsonPath("$.item.id").value(item.getId()));
    }

    @Test
    void updateBooking() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(APPROVED.name()));
    }

    @Test
    void updateBookingByNonOwnerShouldFail() throws Exception {
        // Создаем владельца вещи
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        // Создаем пользователя, который будет бронировать
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        // Создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        // Создаем третьего пользователя (не владельца и не бронирующего)
        User otherUser = createUser();
        MultiValueMap<String, String> otherUserHeaders = createHeaders(X_SHARER_USER_ID, otherUser.getId().toString());

        // Пытаемся подтвердить бронирование от имени третьего пользователя
        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", otherUserHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void getBooking() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings/" + booking.getId(), bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUser() throws Exception {
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings", bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);
        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings/owner", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createBookingForUnavailableItem() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item unavailableItem = createItem(ownerHeaders, false);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        String jsonBooking = createJson(bookingDtoToMap(RandomUtils.getBooking(unavailableItem.getId(), start, end)));

        performRequest(POST, "/bookings", jsonBooking, bookerHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.error").value("Вещь недоступна для бронирования"));
    }

    @Test
    void createBookingWithWrongUserId() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        MultiValueMap<String, String> wrongUserHeaders = createHeaders(X_SHARER_USER_ID, "999");

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        String jsonBooking = createJson(bookingDtoToMap(RandomUtils.getBooking(item.getId(), start, end)));

        performRequest(POST, "/bookings", jsonBooking, wrongUserHeaders)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void getNonExistentBooking() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        performRequest(GET, "/bookings/999", headers)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void updateBookingWithWrongUserId() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        MultiValueMap<String, String> wrongUserHeaders = createHeaders(X_SHARER_USER_ID, "999");

        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=true", wrongUserHeaders)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void getBookingsByUserCurrent() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);

        createBooking(bookerHeaders, item.getId(), start, end);
        Thread.sleep(10000);
        performRequest(GET, "/bookings?state=" + CURRENT, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUserFuture() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(60);

        createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings?state=" + FUTURE, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUserWaiting() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(60);

        createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings?state=" + WAITING, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByUserRejected() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(60);

        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=false", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(REJECTED.name()));

        performRequest(GET, "/bookings?state=" + REJECTED, bookerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(REJECTED.name()));
    }

    @Test
    void getBookingsByOwnerCurrent() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);

        createBooking(bookerHeaders, item.getId(), start, end);
        Thread.sleep(10000);

        performRequest(GET, "/bookings/owner?state=" + CURRENT, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerPast() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusSeconds(1);
        LocalDateTime end = LocalDateTime.now().plusSeconds(3);

        createBooking(bookerHeaders, item.getId(), start, end);
        Thread.sleep(10000);

        performRequest(GET, "/bookings/owner?state=" + PAST, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerFuture() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);

        createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings/owner?state=" + FUTURE, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerWaiting() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);

        createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(GET, "/bookings/owner?state=" + WAITING, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(WAITING.name()));
    }

    @Test
    void getBookingsByOwnerRejected() throws Exception {
        User owner = createUser();
        MultiValueMap<String, String> ownerHeaders = createHeaders(X_SHARER_USER_ID, owner.getId().toString());
        Item item = createItem(ownerHeaders, true);

        User booker = createUser();
        MultiValueMap<String, String> bookerHeaders = createHeaders(X_SHARER_USER_ID, booker.getId().toString());

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);

        Booking booking = createBooking(bookerHeaders, item.getId(), start, end);

        performRequest(PATCH, "/bookings/" + booking.getId() + "?approved=false", ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(REJECTED.name()));

        performRequest(GET, "/bookings/owner?state=" + REJECTED, ownerHeaders)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(REJECTED.name()));
    }
}