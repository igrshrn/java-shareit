package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;

class UserUpdateDtoTest extends BaseDtoTest<UserUpdateDto> {
    @Test
    void testSerialize() throws IOException {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        JsonContent<UserUpdateDto> result = json.write(dto);

        assertJsonField(result, "@.name", "Updated Name");
        assertJsonField(result, "@.email", "updated@example.com");
    }

    @Test
    void testValidationSuccess() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Name")
                .email("user@example.com")
                .build();

        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationSuccessWithNullFields() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(null)
                .email(null)
                .build();

        assertNullFieldsValid(dto);
    }

    @Test
    void testInvalidEmailFormat() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, Email.class);
    }

    @Test
    void testPartialUpdate() throws IOException {
        UserUpdateDto dto = UserUpdateDto.builder()
                .email("only-email@example.com")
                .build();

        JsonContent<UserUpdateDto> result = json.write(dto);

        assertJsonField(result, "@.email", "only-email@example.com");
        assertJsonField(result, "@.name", null);
    }
}