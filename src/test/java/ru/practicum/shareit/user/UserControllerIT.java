package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    @SneakyThrows
    @Test
    void addUser_whenValid_thenStatusIsOk() {
        UserDto userDtoToAdd = UserDto.builder()
                .name("Test")
                .email("test@test.ru")
                .build();
        when(userService.addUser(userDtoToAdd)).thenReturn(userDtoToAdd);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToAdd)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToAdd), result);
    }

    @SneakyThrows
    @Test
    void addUser_whenUserIsNotValid_thenStatusIsBadRequest() {
        UserDto userDtoToAdd = UserDto.builder().build();
        when(userService.addUser(userDtoToAdd)).thenReturn(userDtoToAdd);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToAdd)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(userService, never()).addUser(userDtoToAdd);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Test")
                .email("test@test.ru")
                .build();
        when(userService.updateUser(userId, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getUserById() {
        Long userId = 0L;

        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userRepository).findAll();
    }

    @SneakyThrows
    @Test
    void deleteUserById() {
        Long userId = 0L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userRepository).deleteById(userId);
    }
}