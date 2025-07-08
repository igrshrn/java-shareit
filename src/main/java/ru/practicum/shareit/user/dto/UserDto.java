package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;

    @NotBlank(message = "Email не может быть пустым или содержать пробелы")
    @Email(message = "Email должен быть в правильном формате")
    private String email;
}
