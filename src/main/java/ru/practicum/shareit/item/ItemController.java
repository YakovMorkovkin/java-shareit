package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static ru.practicum.shareit.Constants.ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

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
    public List<ItemDto> getAllItemsOfUserWithId(@NotEmpty @RequestHeader(ID) Long userId,
                                                 @RequestParam(value = "from", defaultValue = "0",
                                                         required = false) @Min(0) Integer offset,
                                                 @RequestParam(value = "size", defaultValue = "10",
                                                         required = false) @Min(1) @Max(50) Integer limit) {
        return itemService.getAllItemsOfUserWithId(userId, offset, limit);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(value = "from", defaultValue = "0",
                                            required = false) @Min(0) Integer offset,
                                    @RequestParam(value = "size", defaultValue = "10",
                                            required = false) @Min(1) @Max(50) Integer limit) {
        return itemService.searchItem(text, offset, limit);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @NotEmpty @RequestHeader(ID) Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
