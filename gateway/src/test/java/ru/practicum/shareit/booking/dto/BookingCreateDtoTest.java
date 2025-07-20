package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.BaseDtoTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoTest extends BaseDtoTest<BookingCreateDto> {

    @Test
    void testSerialize() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2).withNano(0);

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("@.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("@.end").isEqualTo(end.toString());
    }

    @Test
    void testValidationSuccess() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFail() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
    }

    @Test
    void testValidBookingDatesFail() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);

        Set<String> properties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(properties).containsExactlyInAnyOrder("end", "start");
    }

    @Test
    void testAllConstraints() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);

        Set<String> properties = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(properties).containsExactlyInAnyOrder("itemId", "start", "end");

        Map<String, String> propertyToMessage = violations.stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage));

        assertThat(propertyToMessage.get("itemId")).isEqualTo("не должно равняться null");
        assertThat(propertyToMessage.get("start")).isEqualTo("не должно равняться null");
        assertThat(propertyToMessage.get("end")).isEqualTo("не должно равняться null");
    }

    @Test
    void whenItemIdIsNull_thenViolationForItemId() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);

        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();

        assertThat(violation.getPropertyPath().toString()).isEqualTo("itemId");
        assertThat(violation.getMessage()).isEqualTo("не должно равняться null");
    }

    @Test
    void whenStartIsNull_thenViolationForStart() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);

        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();

        assertThat(violation.getPropertyPath().toString()).isEqualTo("start");
        assertThat(violation.getMessage()).isEqualTo("не должно равняться null");
    }

    @Test
    void whenEndIsNull_thenViolationForEnd() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);

        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();

        assertThat(violation.getPropertyPath().toString()).isEqualTo("end");
        assertThat(violation.getMessage()).isEqualTo("не должно равняться null");
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

        assertThat(violations).hasSize(1);

        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();

        assertThat(violation.getMessage()).isEqualTo(
                "Дата начала бронирования должна быть раньше даты окончания");
    }

    @Test
    void whenStartAfterEnd_thenViolationForDates() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);

        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();

        assertThat(violation.getMessage()).isEqualTo(
                "Дата начала бронирования должна быть раньше даты окончания");
    }

    @Test
    void whenStartBeforeEnd_thenNoViolationForDates() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}