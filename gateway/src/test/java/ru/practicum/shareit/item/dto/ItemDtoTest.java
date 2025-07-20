package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoTest extends BaseDtoTest<ItemDto> {
    @Test
    void testSerialize() throws IOException {
        ItemDto dto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("@.available").isEqualTo(dto.getAvailable());
    }

    @Test
    void testValidationSuccess() {
        ItemDto dto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFail() {
        ItemDto dto = ItemDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(messages).containsExactlyInAnyOrder(
                "Наименование не может быть пустым",
                "Описание не может быть пустым",
                "Доступность аренды должна быть указана"
        );
    }

    @Test
    void testWithAllFields() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(new User())
                .itemRequest(new ItemRequest())
                .lastBooking(new Booking())
                .nextBooking(new Booking())
                .comments(List.of(new CommentForItemDto()))
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}