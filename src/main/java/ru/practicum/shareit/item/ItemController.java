package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto addItem(@NotEmpty @RequestHeader(ID) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @NotEmpty @RequestHeader(ID) Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@NotEmpty @RequestHeader(ID) Long userId,
                               @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUserWithId(@NotEmpty @RequestHeader(ID) Long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toDTO)
                .map(itemService::connectBooking)
                .map(itemService::connectComment)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        if (text != null && !text.isEmpty()) {
            return itemRepository.findByDescriptionContainsIgnoreCaseOrNameContainsIgnoreCase(text, text)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(itemMapper::toDTO)
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @NotEmpty @RequestHeader(ID) Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
