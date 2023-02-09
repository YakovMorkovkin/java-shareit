package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private Long id;
    @NotEmpty
    private final LocalDateTime start;
    @NotEmpty
    private final LocalDateTime end;
    @NotEmpty
    private final Item item;
    @NotEmpty
    private final User booker;
    @NotEmpty
    private final Status status;
}
