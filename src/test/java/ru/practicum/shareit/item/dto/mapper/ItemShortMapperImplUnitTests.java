package ru.practicum.shareit.item.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemShortMapperImplUnitTests {
    @Autowired
    ItemShortMapperImpl itemMapper;

    LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 1);

    User testOwner = User.builder()
            .id(1L)
            .name("Test")
            .email("test@email.ru")
            .build();

    User testRequestor = User.builder()
            .id(2L)
            .name("Test1")
            .email("test1@email.ru")
            .build();

    ItemRequest testItemRequest = ItemRequest.builder()
            .id(1L)
            .description("test request")
            .requestor(testRequestor)
            .created(dateTime)
            .build();

    Item testItem = Item.builder()
            .id(1L)
            .name("Item")
            .owner(testOwner)
            .description("test Item")
            .available(true)
            .itemRequest(testItemRequest)
            .build();
    ItemShortDto testItemShortDto = ItemShortDto.builder()
            .id(1L)
            .name("Item")
            .ownerId(testOwner.getId())
            .description("test Item")
            .available(true)
            .requestId(testItemRequest.getId())
            .build();

    @Test
    void toDTO_whenItemNotNull_thenReturnItemShortDto() {
        ItemShortDto actualItemShortDto = itemMapper.toDTO(testItem);
        assertEquals(testItemShortDto.getId(), actualItemShortDto.getId());
        assertEquals(testItemShortDto.getName(), actualItemShortDto.getName());
        assertEquals(testItemShortDto.getOwnerId(), actualItemShortDto.getOwnerId());
        assertEquals(testItemShortDto.getDescription(), actualItemShortDto.getDescription());
        assertEquals(testItemShortDto.getAvailable(), actualItemShortDto.getAvailable());
        assertEquals(testItemShortDto.getRequestId(), actualItemShortDto.getRequestId());
    }

    @Test
    void toDTO_whenItemNull_thenReturnNull() {
        ItemShortDto actualItemShortDto = itemMapper.toDTO(null);
        assertNull(actualItemShortDto);
    }
}