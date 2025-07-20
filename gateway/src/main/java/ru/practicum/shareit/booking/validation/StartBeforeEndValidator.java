package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

public class StartBeforeEndValidator implements ConstraintValidator<ValidBookingDates, BookingCreateDto> {
    @Override
    public boolean isValid(BookingCreateDto bookingCreateDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingCreateDto.getStart() == null || bookingCreateDto.getEnd() == null) {
            return true;
        }
        return bookingCreateDto.getStart().isBefore(bookingCreateDto.getEnd());
    }
}
