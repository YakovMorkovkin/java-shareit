package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    private long id;
    @NotEmpty
    private LocalDateTime start;
    @NotEmpty
    private LocalDateTime end;
    @NotEmpty
    private Item item;
    @NotEmpty
    private User booker;
    @NotEmpty
    private Status status;
}
