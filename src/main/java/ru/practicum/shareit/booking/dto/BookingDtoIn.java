package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoIn {
    @NotNull
    private Long itemId;
    @NotNull
    @Future
    private final LocalDateTime start;
    @NotNull
    @Future
    private final LocalDateTime end;
}
