package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Service
public interface ItemService {
    Item composeItem(Item item, ItemDto itemDto);

    ItemDto connectBooking(ItemDto itemDto);

    ItemDto connectComment(ItemDto itemDto);

}
