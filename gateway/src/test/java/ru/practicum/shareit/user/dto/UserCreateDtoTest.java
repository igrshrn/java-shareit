package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.util.Set;

class UserCreateDtoTest extends BaseDtoTest<UserCreateDto> {

    @Test
    void testSerialize() throws IOException {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Name")
                .email("user@example.com")
                .build();

        JsonContent<UserCreateDto> result = json.write(dto);

        assertJsonField(result, "@.name", "Name");
        assertJsonField(result, "@.email", "user@example.com");
    }

    @Test
    void testValidationSuccess() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Name")
                .email("valid@example.com")
                .build();

        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationFailEmptyFields() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("")
                .email("")
                .build();

        assertValidationFailsWithEmptyFields(dto, 2, NotBlank.class, NotBlank.class);
    }

    @Test
    void testInvalidEmailFormat() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Name")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, Email.class);
    }

    @Test
    void testNullFieldsValidation() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(null)
                .email(null)
                .build();

        assertValidationFailsWithEmptyFields(dto, 2, NotBlank.class, NotBlank.class);
    }
}