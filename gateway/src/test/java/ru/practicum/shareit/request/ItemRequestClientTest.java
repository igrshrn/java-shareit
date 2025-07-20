package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.AbstractClientTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ItemRequestClientTest extends AbstractClientTest {

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:8080", restTemplateBuilder);
    }

    @Test
    void createItem() {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Item Request").build();

        mockExchange("", HttpMethod.POST, mockResponse);

        ResponseEntity<Object> response = itemRequestClient.createItem(1L, itemRequestCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("", HttpMethod.POST, 1L, itemRequestCreateDto, true);
    }

    @Test
    void getUserRequests() {
        mockExchange("", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = itemRequestClient.getUserRequests(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("", HttpMethod.GET, 1L, null, true);

    }

    @Test
    void getRequestById() {
        mockExchange("/1", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = itemRequestClient.getRequestById(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.GET, 1L, null, true);
    }
}