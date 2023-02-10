package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@Builder
public class UserDto implements Serializable {
    private final Long id;
    private final String name;
    @NotEmpty
    @Email
    private final String email;
}
