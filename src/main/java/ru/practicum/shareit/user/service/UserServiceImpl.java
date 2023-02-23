package ru.practicum.shareit.user.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@Primary
public class UserServiceImpl implements UserService {
    @Override
    public User composeUser(User user, UserDto userDto) {
        return User.builder()
                .id(user.getId())
                .name(userDto.getName() != null ? userDto.getName() : user.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail())
                .build();
    }
}
