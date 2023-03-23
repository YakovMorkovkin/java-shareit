package ru.practicum.shareit.user.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class UserMapperImplUnitTests {

    @Autowired
    UserMapperImpl userMapper;
    User testUser = User.builder()
            .id(1L)
            .name("Test")
            .email("test@email.ru")
            .build();

    UserDto testUserDto = UserDto.builder()
            .id(1L)
            .name("Test")
            .email("test@email.ru")
            .build();


    @Test
    void toDTO_whenUserNotNull_thenReturnUserDto() {
        UserDto actualUserDto = userMapper.toDTO(testUser);
        assertEquals(testUserDto.getId(), actualUserDto.getId());
        assertEquals(testUserDto.getName(), actualUserDto.getName());
        assertEquals(testUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void toDTO_whenUserNull_thenReturNull() {
        UserDto actualUserDto = userMapper.toDTO(null);
        assertNull(actualUserDto);
    }

    @Test
    void toModel_whenUserDtoNotNull_thenReturnUser() {
        User actualUser = userMapper.toModel(testUserDto);
        assertEquals(testUser.getId(), actualUser.getId());
        assertEquals(testUser.getName(), actualUser.getName());
        assertEquals(testUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void toModel_whenUserDtoNull_thenReturNull() {
        User actualUser = userMapper.toModel(null);
        assertNull(actualUser);
    }
}