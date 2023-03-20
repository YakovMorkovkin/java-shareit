package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getAllItemsOfUserWithId(Long userId, Integer offset, Integer limit);

    List<ItemDto> searchItem(String text, Integer offset, Integer limit);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

    Item getItem(Long itemId);

    ItemDto connectBooking(ItemDto itemDto);

    ItemDto connectComment(ItemDto itemDto);


}
