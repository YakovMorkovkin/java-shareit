package ru.practicum.shareit.item.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemMapperImplUnitTests {

    @Autowired
    ItemMapperImpl itemMapper;

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
    ItemDto testItemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("test Item")
            .available(true)
            .requestId(1L)
            .build();

    @Test
    void toDTO_whenItemNotNull_thenReturnItemDto() {
        ItemDto actualItemDto = itemMapper.toDTO(testItem);
        assertEquals(testItemDto.getId(), actualItemDto.getId());
        assertEquals(testItemDto.getName(), actualItemDto.getName());
        assertEquals(testItemDto.getDescription(), actualItemDto.getDescription());
        assertEquals(testItemDto.getAvailable(), actualItemDto.getAvailable());
        assertEquals(testItemDto.getRequestId(), actualItemDto.getRequestId());
    }

    @Test
    void toDTO_whenItemNull_thenReturnNull() {
        ItemDto actualItemDto = itemMapper.toDTO(null);
        assertNull(actualItemDto);
    }

    @Test
    void toModel_whenItemDtoNotNull_thenReturnItem() {
        Item actualItem = itemMapper.toModel(testItemDto);
        assertEquals(testItemDto.getId(), actualItem.getId());
        assertEquals(testItemDto.getName(), actualItem.getName());
        assertEquals(testItemDto.getDescription(), actualItem.getDescription());
        assertEquals(testItemDto.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void toModel_whenItemDtoNull_thenReturnNull() {
        Item actualItem = itemMapper.toModel(null);
        assertNull(actualItem);
    }
}