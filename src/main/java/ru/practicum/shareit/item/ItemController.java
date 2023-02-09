package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto addItem(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemRepository.addItem(userId, itemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemRepository.updateItem(itemId, userId, itemMapper.toItem(itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemMapper.toItemDto(itemRepository.getItemById(itemId).orElseThrow(
                () -> new ItemFoundException(itemId)
        ));
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUserWithId(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllUserItems(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItemBySubString(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
