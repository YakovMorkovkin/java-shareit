package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoIn {
    private Long itemId;
    private final LocalDateTime start;
    private final LocalDateTime end;
}
