package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    User getUser(Long userId);

    User composeUser(User user, UserDto userDto);
}
