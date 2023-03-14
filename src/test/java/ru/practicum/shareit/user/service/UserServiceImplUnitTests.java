package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void updateUser_whenWrongUser_thenUserNotFoundExceptionThrown() {
        Long userId = 2L;
        UserDto testUserDto = UserDto.builder()
                .email("email@email.ru")
                .name("test User")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, testUserDto));
    }

    @Test
    void updateUser_whenDuplicateEmail_thenEmailDuplicateExceptionThrown() {
        Long userId = 2L;
        User testUser = User.builder()
                .id(1L)
                .email("email@email.ru")
                .name("test User")
                .build();
        UserDto testUserDto = UserDto.builder()
                .email("email@email.ru1")
                .name("test User")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));

        assertThrows(EmailDuplicateException.class, () -> userService.updateUser(userId, testUserDto));
    }

    @Test
    void updateUser_whenDataCorrect_thenUpdateUser() {
        Long userId = 1L;
        User testUser = User.builder()
                .id(1L)
                .email("email@email.ru")
                .name("test User")
                .build();
        UserDto testUserDto = UserDto.builder()
                .email("email@email.ru")
                .name("test User1")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        userService.updateUser(userId, testUserDto);

        verify(userMapper).toDTO(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertEquals(testUserDto.getName(), updatedUser.getName());
    }
}