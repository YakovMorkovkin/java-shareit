package ru.practicum.shareit.request.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemRequestMapperImplUnitTests {
    @Autowired
    ItemRequestMapperImpl itemRequestMapper;

    LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 1);

    User requestor = User.builder()
            .id(1L)
            .name("Test")
            .email("test@email.ru")
            .build();

    ItemRequest testItemRequest = ItemRequest.builder()
            .id(1L)
            .description("test request")
            .requestor(requestor)
            .created(dateTime)
            .build();

    ItemRequestDto testItemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("test request")
            .requestorId(1L)
            .created(dateTime)
            .build();

    @Test
    void toDTO_whenItemRequestNotNull_thenReturnItemRequestDto() {
        ItemRequestDto actualItemRequestDto = itemRequestMapper.toDTO(testItemRequest);
        assertEquals(testItemRequestDto.getId(), actualItemRequestDto.getId());
        assertEquals(testItemRequestDto.getDescription(), actualItemRequestDto.getDescription());
        assertEquals(testItemRequestDto.getRequestorId(), actualItemRequestDto.getRequestorId());
        assertEquals(testItemRequestDto.getCreated(), actualItemRequestDto.getCreated());
    }

    @Test
    void toDTO_whenItemRequestNull_thenReturnNull() {
        ItemRequestDto actualItemRequestDto = itemRequestMapper.toDTO(null);
        assertNull(actualItemRequestDto);
    }

    @Test
    void toModel_whenItemRequestDtoNotNull_thenReturnItemRequest() {
        ItemRequest testItemRequest = itemRequestMapper.toModel(testItemRequestDto);
        assertEquals(testItemRequestDto.getId(), testItemRequest.getId());
        assertEquals(testItemRequestDto.getDescription(), testItemRequest.getDescription());
        assertEquals(testItemRequestDto.getCreated(), testItemRequest.getCreated());
    }

    @Test
    void toModel_whenItemRequestDtoNull_thenReturnNull() {
        ItemRequest testItemRequest = itemRequestMapper.toModel(null);
        assertNull(testItemRequest);
    }
}