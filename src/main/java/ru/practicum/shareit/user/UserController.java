package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userMapper.toDTO(userRepository.save(userMapper.toModel(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        Optional<User> userToUpdate = userRepository.findById(userId);
        User updateUser = new User();
        if ((userToUpdate.orElseThrow(() -> new UserFoundException(userId)).getEmail().equals(userDto.getEmail()) ||
                !userRepository.findByEmail(userDto.getEmail()).isPresent())) {
            updateUser = userService.composeUser(userToUpdate.get(), userDto);
            userRepository.update(updateUser.getName(), updateUser.getEmail(), updateUser.getId());
        } else if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EmailDuplicateException(userDto.getEmail());
        }
        return userMapper.toDTO(updateUser);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userMapper.toDTO(userRepository.findById(userId).orElseThrow(
                () -> new UserFoundException(userId)
        ));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userRepository.deleteById(userId);
    }
}
