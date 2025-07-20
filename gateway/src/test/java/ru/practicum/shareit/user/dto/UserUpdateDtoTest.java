package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateDtoTest extends BaseDtoTest<UserUpdateDto> {
    @Test
    void testSerialize() throws IOException {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        JsonContent<UserUpdateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.name")
                .isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("@.email")
                .isEqualTo(dto.getEmail());
    }

    @Test
    void testValidationSuccess() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Name")
                .email("user@example.com")
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationSuccessWithNullFields() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(null)
                .email(null)
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidEmailFormat() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email должен быть в правильном формате");
    }

    @Test
    void testPartialUpdate() throws IOException {
        UserUpdateDto dto = UserUpdateDto.builder()
                .email("only-email@example.com")
                .build();

        JsonContent<UserUpdateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.email")
                .isEqualTo("only-email@example.com");
        assertThat(result).extractingJsonPathStringValue("@.name")
                .isNull();
    }
}