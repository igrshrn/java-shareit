package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    private String name;

    @Email(message = "Email должен быть в правильном формате")
    private String email;
}
