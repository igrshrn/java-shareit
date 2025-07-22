package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;

class ItemRequestCreateDtoTest extends BaseDtoTest<ItemRequestCreateDto> {

    @Test
    void testSerialize() throws IOException {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("Description")
                .build();

        JsonContent<ItemRequestCreateDto> result = json.write(dto);
        assertJsonField(result, "@.description", "Description");
    }

    @Test
    void testValidationSuccess() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("Valid description")
                .build();

        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationFail() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotBlank.class);
    }

    @Test
    void testNullDescriptionValidation() {
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description(null)
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotBlank.class);
    }
}