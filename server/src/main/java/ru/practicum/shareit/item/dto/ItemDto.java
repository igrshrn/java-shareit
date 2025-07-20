package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;

    @JsonIdentityReference(alwaysAsId = true)
    private ItemRequest itemRequest;

    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentForItemDto> comments;
}
