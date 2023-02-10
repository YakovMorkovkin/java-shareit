package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@Builder
public class User implements Serializable {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email
    private String email;
}
