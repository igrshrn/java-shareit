package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommentCreateDtoTest extends BaseDtoTest<CommentCreateDto> {
    @Test
    void testSerialize() throws IOException {
        String text = "Comment";
        CommentCreateDto dto = CommentCreateDto.builder()
                .text(text)
                .build();

        JsonContent<CommentCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.text").isEqualTo(text);
    }

    @Test
    void testValidationSuccess() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("Comment")
                .build();

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFail() {
        CommentCreateDto dto = new CommentCreateDto("");
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("не должно быть пустым");
    }

    @Test
    void testNullTextValidation() {
        CommentCreateDto dto = new CommentCreateDto(null);
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("не должно быть пустым");
    }
}