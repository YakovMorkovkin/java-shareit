package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private long id;
    @NotEmpty
    private String description;
    @NotEmpty
    private User requestor;
    @NotEmpty
    private LocalDateTime created;
}
