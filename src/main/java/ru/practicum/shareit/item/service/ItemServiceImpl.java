package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getAllUserItems(Long userId) {
        userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("User with id -" + userId + " not found"));
        return itemRepository.getAllItems().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemBySubString(String subString) {
        return itemRepository.getAllItems().stream()
                .filter(Item::getAvailable)
                .filter(i -> !subString.isEmpty() && (i.getName().toLowerCase().contains(subString.toLowerCase())
                        || i.getDescription().toLowerCase().contains(subString.toLowerCase())))
                .collect(Collectors.toList());
    }
}
