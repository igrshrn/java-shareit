package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;
import ru.practicum.shareit.booking.validation.ValidBookingDates;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@JsonTest
class BookingCreateDtoTest extends BaseDtoTest<BookingCreateDto> {

    @Test
    void testSerialize() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2).withNano(0);

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingCreateDto> result = json.write(dto);

        assertJsonField(result, "@.itemId", 1);
        assertJsonField(result, "@.start", start.toString());
        assertJsonField(result, "@.end", end.toString());
    }

    @Test
    void testValidationSuccess() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();

        assertNullFieldsValid(dto);
    }

    @Test
    void testValidationFail() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        assertValidationFailsWithEmptyFields(dto, 3, NotNull.class, NotNull.class, NotNull.class);
    }

    @Test
    void whenItemIdIsNull_thenViolationForItemId() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, FutureOrPresent.class);
    }

    @Test
    void whenStartIsNull_thenViolationForStart() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotNull.class);
    }

    @Test
    void whenEndIsNull_thenViolationForEnd() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, NotNull.class);
    }

    // Кастомная валидация @ValidBookingDates
    @Test
    void whenStartEqualsEnd_thenViolationForDates() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(now)
                .end(now)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, ValidBookingDates.class);
    }

    @Test
    void whenStartAfterEnd_thenViolationForDates() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertViolationHasAnnotation(violations, ValidBookingDates.class);
    }
}