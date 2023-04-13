package ru.practicum.shareit.user.dto.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDTO(User model);

    User toModel(UserDto dto);

}
