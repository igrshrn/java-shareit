package ru.practicum.shareit.request;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
