package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreateDtoTest extends BaseDtoTest<UserCreateDto> {

    @Test
    void testSerialize() throws IOException {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Name")
                .email("user@example.com")
                .build();

        JsonContent<UserCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("@.name")
                .isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("@.email")
                .isEqualTo(dto.getEmail());
    }

    @Test
    void testValidationSuccess() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Name")
                .email("valid@example.com")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFailEmptyFields() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("")
                .email("")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(messages).containsExactlyInAnyOrder(
                "не должно быть пустым",
                "Email не может быть пустым или содержать пробелы"
        );
    }

    @Test
    void testInvalidEmailFormat() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Name")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email должен быть в правильном формате");
    }

    @Test
    void testNullFieldsValidation() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(null)
                .email(null)
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(messages).containsExactlyInAnyOrder(
                "не должно быть пустым",
                "Email не может быть пустым или содержать пробелы"
        );
    }
}