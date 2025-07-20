package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestCreateDtoTest extends BaseDtoTest<ItemRequestCreateDto> {

    @Test
    void testSerialize() throws IOException {
        String description = "Description";
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description(description)
                .build();

        JsonContent<ItemRequestCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.description")
                .isEqualTo(description);
    }

    @Test
    void testValidationSuccess() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("Valid description")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFail() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("не должно быть пустым");
    }

    @Test
    void testNullDescriptionValidation() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description(null)
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("не должно быть пустым");
    }
}