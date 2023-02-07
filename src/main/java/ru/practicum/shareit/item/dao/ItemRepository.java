package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
@Repository
public interface ItemRepository {

    Item addItem(Long userId, Item item);
    Item updateItem(Long itemId, Long userId, Item item);
    List<Item> getAllItems();
    Optional<Item> getItemById(Long itemId);


}
