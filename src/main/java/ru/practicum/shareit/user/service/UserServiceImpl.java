package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("Add user");
        return userMapper.toDTO(userRepository.save(userMapper.toModel(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userToUpdate = getUser(userId);
        User updateUser = new User();
        if ((userToUpdate.getEmail().equals(userDto.getEmail()) ||
                !userRepository.findByEmail(userDto.getEmail()).isPresent())) {
            updateUser = composeUser(userToUpdate, userDto);
            userRepository.update(updateUser.getName(), updateUser.getEmail(), updateUser.getId());
            log.info("Data for user with id-{} updated", userId);
        } else if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EmailDuplicateException(userDto.getEmail());
        }
        return userMapper.toDTO(updateUser);
    }

    @Override
    public User getUser(Long userId) {
        log.info("Getting user with id-{}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private User composeUser(User user, UserDto userDto) {
        return User.builder()
                .id(user.getId())
                .name(userDto.getName() != null ? userDto.getName() : user.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail())
                .build();
    }
}
