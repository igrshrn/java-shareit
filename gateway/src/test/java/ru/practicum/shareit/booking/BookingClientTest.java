package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.AbstractClientTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingClientTest extends AbstractClientTest {
    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("http://localhost:8080", restTemplateBuilder);
    }

    @Test
    void createBooking() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockExchange("", HttpMethod.POST, mockResponse);

        ResponseEntity<Object> response = bookingClient.createBooking(1L, bookingCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("", HttpMethod.POST, 1L, bookingCreateDto, true);
    }

    @Test
    void updateBooking() {
        mockExchange("/1?approved=true", HttpMethod.PATCH, mockResponse);

        ResponseEntity<Object> response = bookingClient.updateBooking(1L, 1L, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1?approved=true", HttpMethod.PATCH, 1L, null, true);
    }

    @Test
    void getBooking() {
        mockExchange("/1", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.GET, 1L, null, true);
    }

    @Test
    void getBookingsByUser() {
        mockExchange("?state=ALL", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = bookingClient.getBookingsByUser(1L, BookingState.ALL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("?state=ALL", HttpMethod.GET, 1L, null, true);
    }

    @Test
    void getBookingsByOwner() {
        mockExchange("/owner?state=ALL", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = bookingClient.getBookingsByOwner(1L, BookingState.ALL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/owner?state=ALL", HttpMethod.GET, 1L, null, true);
    }
}