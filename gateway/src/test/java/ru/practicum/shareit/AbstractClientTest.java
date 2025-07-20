package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractClientTest {
    @Mock
    protected RestTemplate restTemplate;

    @Mock
    protected RestTemplateBuilder restTemplateBuilder;

    protected ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();

    @BeforeEach
    void setUpBase() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
    }

    protected void verifyExchange(
            String expectedUrl,
            HttpMethod expectedMethod,
            Long expectedUserId,
            Object expectedBody,
            boolean checkStandardHeaders) {
        verify(restTemplate).exchange(
                eq(expectedUrl),
                eq(expectedMethod),
                argThat(entity -> {
                    // Проверка заголовка пользователя
                    boolean userIdValid = expectedUserId == null ||
                            Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"),
                                    expectedUserId.toString());

                    // Проверка тела запроса
                    boolean bodyValid = expectedBody == null || entity.getBody() == expectedBody;

                    // Проверка стандартных заголовков
                    boolean headersValid = true;
                    if (checkStandardHeaders) {
                        headersValid = Objects.equals(entity.getHeaders().getFirst("Content-Type"), "application/json") &&
                                Objects.equals(entity.getHeaders().getFirst("Accept"), "application/json");
                    }

                    return userIdValid && bodyValid && headersValid;
                }),
                eq(Object.class));
    }

    protected void mockExchange(String url, HttpMethod method, ResponseEntity<Object> response) {
        when(restTemplate.exchange(
                eq(url),
                eq(method),
                any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(response);
    }
}