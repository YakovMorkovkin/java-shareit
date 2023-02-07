package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserMismatchException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.validation.Valid;
import java.util.*;

@Repository("ItemRepositoryImpl")
@RequiredArgsConstructor
@Primary
public class ItemRepositoryImpl implements ItemRepository {
    private Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();
    private final UserRepository userRepository;


    @Override
    public Item addItem(Long userId, @Valid Item item) {
        item.setOwner(userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("User with id -" + userId + " isn't found")));
        Long key = generatorId();
        item.setId(key);
        items.put(key, item);
        return items.get(key);
    }

    @Override
    public Item updateItem(Long itemId, Long userId, Item newItem) {
        Item originalItem;
        if (userId.equals(items.get(itemId).getOwner().getId())) {
            originalItem = items.get(itemId);
        } else {
            throw new UserMismatchException("User with id-" + userId + " isn't owner item with id -" + itemId);
        }
        items.put(itemId, Item.builder()
                .id(originalItem.getId())
                .owner(originalItem.getOwner())
                .name(newItem.getName() != null ? newItem.getName() : originalItem.getName())
                .description(newItem.getDescription() != null ? newItem.getDescription() : originalItem.getDescription())
                .available(newItem.getAvailable() != null ? newItem.getAvailable() : originalItem.getAvailable())
                .request(originalItem.getRequest())
                .build());
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    private Long generatorId() {
        return ++id;
    }

}
