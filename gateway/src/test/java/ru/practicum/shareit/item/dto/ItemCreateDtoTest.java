package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCreateDtoTest extends BaseDtoTest<ItemCreateDto> {

    @Test
    void testSerialize() throws IOException {

        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        JsonContent<ItemCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("@.available").isEqualTo(dto.getAvailable());
    }

    @Test
    void testValidationSuccess() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFail() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);

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
    void testRequestIdValidation() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(0L)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("ID запроса должен быть положительным");
    }
}