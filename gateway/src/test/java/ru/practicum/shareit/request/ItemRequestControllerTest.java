package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ru.practicum.shareit.AbstractControllerTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest extends AbstractControllerTest<ItemRequestClient> {
    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Description")
                .build();
    }

    @Test
    void createItemRequest_whenValid_thenStatusOk() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<ItemRequestCreateDto> captor = ArgumentCaptor.forClass(ItemRequestCreateDto.class);
        verify(client).createItem(eq(1L), captor.capture());

        assertEquals(itemRequestCreateDto.getDescription(), captor.getValue().getDescription());
    }

    @Test
    void createItemRequest_whenMissingHeader_thenStatusBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemRequest_whenInvalidData_thenStatusBadRequest() throws Exception {
        ItemRequestCreateDto invalidDto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_whenValid_thenStatusOk() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(client).getUserRequests(eq(1L));
    }

    @Test
    void getUserRequests_whenMissingHeader_thenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_whenValid_thenStatusOk() throws Exception {
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(client).getRequestById(eq(1L), eq(1L));
    }

    @Test
    void handleException_shouldReturnInternalServerError() throws Exception {
        when(client.createItem(anyLong(), any(ItemRequestCreateDto.class)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isInternalServerError());
    }
}