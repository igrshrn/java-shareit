package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.AbstractClientTest;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemClientTest extends AbstractClientTest {

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:8080", restTemplateBuilder);
    }

    @Test
    void createItem() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        mockExchange("", HttpMethod.POST, mockResponse);

        ResponseEntity<Object> response = itemClient.createItem(1L, itemCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("", HttpMethod.POST, 1L, itemCreateDto, true);
    }

    @Test
    void updateItem() {
        ItemDto itemDto = ItemDto.builder().name("Updated item").build();

        mockExchange("/1", HttpMethod.PATCH, mockResponse);

        ResponseEntity<Object> response = itemClient.updateItem(1L, 1L, itemDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.PATCH, 1L, itemDto, true);
    }

    @Test
    void getItemById() {
        mockExchange("/1", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = itemClient.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.GET, null, null, true);
    }

    @Test
    void getItemsByOwner() {
        mockExchange("/", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = itemClient.getItemsByOwner(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/", HttpMethod.GET, 1L, null, true);
    }

    @Test
    void searchItems() {
        mockExchange("/search?text=test", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = itemClient.searchItems("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/search?text=test", HttpMethod.GET, null, null, true);
    }

    @Test
    void createComment() {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        mockExchange("/1/comment", HttpMethod.POST, mockResponse);

        ResponseEntity<Object> response = itemClient.createComment(1L, 1L, commentCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1/comment", HttpMethod.POST, 1L, commentCreateDto, true);
    }
}