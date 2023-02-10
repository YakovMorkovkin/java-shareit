package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class ItemDto implements Serializable {
    private final Long id;
    private final User owner;
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    @NotBlank
    private final String description;
    @NotNull
    private final Boolean available;
    private final Long request;
}
