package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    @NotBlank
    private String name;

    @NotBlank(message = "Email не может быть пустым или содержать пробелы")
    @Email(message = "Email должен быть в правильном формате")
    private String email;
}
