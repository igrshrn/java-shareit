package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.AbstractClientTest;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserClientTest extends AbstractClientTest {

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient("http://localhost:8080", restTemplateBuilder);
    }

    @Test
    void createUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("User")
                .email("user@example.com")
                .build();

        mockExchange("", HttpMethod.POST, mockResponse);

        ResponseEntity<Object> response = userClient.createUser(userCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("", HttpMethod.POST, null, userCreateDto, true);
    }

    @Test
    void updateUser() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        mockExchange("/1", HttpMethod.PATCH, mockResponse);

        ResponseEntity<Object> response = userClient.updateUser(1L, userUpdateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.PATCH, null, userUpdateDto, true);
    }

    @Test
    void getUserById() {
        mockExchange("/1", HttpMethod.GET, mockResponse);

        ResponseEntity<Object> response = userClient.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.GET, null, null, true);
    }

    @Test
    void deleteUser() {
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();
        mockExchange("/1", HttpMethod.DELETE, mockResponse);

        ResponseEntity<Object> response = userClient.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyExchange("/1", HttpMethod.DELETE, null, null, true);
    }
}