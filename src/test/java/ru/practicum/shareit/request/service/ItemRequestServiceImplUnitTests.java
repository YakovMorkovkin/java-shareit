package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.mapper.ItemShortMapper;
import ru.practicum.shareit.item.dto.mapper.ItemShortMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTests {
    User testOwner = User.builder()
            .email("email@email.ru")
            .name("test Owner")
            .id(1L)
            .build();
    User testUser = User.builder()
            .email("email@email.ru")
            .name("test User")
            .id(2L)
            .build();
    Item testItem = Item.builder()
            .id(1L)
            .name("test item")
            .description("Test item of test Owner")
            .owner(testOwner)
            .available(true)
            .build();
    List<Item> items = new ArrayList<>();
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemShortMapper itemShortMapper = new ItemShortMapperImpl();
    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void addItemRequest() {
        Long userId = 2L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Description")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Description")
                .build();
        when(itemRequestMapper.toModel(any())).thenReturn(itemRequest);
        when(userService.getUser(any())).thenReturn(testUser);

        itemRequestService.addItemRequest(userId, itemRequestDto);

        verify(itemRequestRepository).save(itemRequestArgumentCaptor.capture());
        ItemRequest savedItemRequest = itemRequestArgumentCaptor.getValue();

        assertEquals(itemRequest.getDescription(), savedItemRequest.getDescription());
        assertEquals(itemRequest.getRequestor().getId(), savedItemRequest.getRequestor().getId());
    }

    @Test
    void connectItems() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Description")
                .build();
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .id(1L)
                .ownerId(1L)
                .available(true)
                .description("Test item of test Owner")
                .requestId(1L)
                .name("test item")
                .build();
        items.add(testItem);
        when(itemRepository.findAllByItemRequestId(any())).thenReturn(items);
        when(itemShortMapper.toDTO(any())).thenReturn(itemShortDto);

        ItemRequestDto result = itemRequestService.connectItems(itemRequestDto);

        assertEquals(1, result.getItems().size());
    }
}