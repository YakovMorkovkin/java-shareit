package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    List<Item> getAllUserItems(Long userId);

    List<Item> searchItemBySubString(String subString);
}
