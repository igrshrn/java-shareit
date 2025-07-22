package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        lenient().when(request.getRequestURI()).thenReturn("/test/endpoint");
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() {
        String errorMessage = "Invalid argument";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), body.getError());
        assertEquals("/test/endpoint", body.getPath());
        assertTrue(body.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                body.getTimestamp().isEqual(LocalDateTime.now().plusSeconds(1)));

        Map<String, String> message = body.getMessage();
        assertEquals("An unexpected error occurred", message.get("error"));
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() {
        String errorMessage = "Not found";
        NotFoundException ex = new NotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals("/test/endpoint", body.getPath());

        Map<String, String> message = body.getMessage();
        assertEquals(errorMessage, message.get("error"));
    }

    @Test
    void handleAlreadyExistsException_shouldReturnConflict() {
        String errorMessage = "Already exists";
        AlreadyExistsException ex = new AlreadyExistsException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAlreadyExists(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals("/test/endpoint", body.getPath());

        Map<String, String> message = body.getMessage();
        assertEquals(errorMessage, message.get("error"));
    }

    @Test
    void handleMissingRequestHeaderException_shouldReturnBadRequest() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getNestedParameterType()).thenReturn((Class) String.class);

        String headerName = "X-Header";
        MissingRequestHeaderException ex = new MissingRequestHeaderException(headerName, parameter);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingRequestHeaderException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals("/test/endpoint", body.getPath());

        Map<String, String> message = body.getMessage();
        assertTrue(message.get("error").contains("Required request header 'X-Header'"));
    }

    @Test
    void handleNotAvailableException_shouldReturnBadRequest() {
        String errorMessage = "Not available";
        NotAvailableException ex = new NotAvailableException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAvailable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> message = response.getBody().getMessage();
        assertEquals(errorMessage, message.get("error"));
    }

    @Test
    void handleWrongException_shouldReturnBadRequest() {
        String errorMessage = "Wrong operation";
        WrongException ex = new WrongException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWrong(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> message = response.getBody().getMessage();
        assertEquals(errorMessage, message.get("error"));
    }
}