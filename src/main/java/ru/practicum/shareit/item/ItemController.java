package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.UserFoundException;
import ru.practicum.shareit.exception.UserMismatchException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

    @PostMapping
    public ItemDto addItem(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isPresent()) {
            Item newItem = itemMapper.toModel(itemDto);
            newItem.setOwner(owner.get());
            return itemMapper.toDTO(itemRepository.save(newItem));
        } else throw new UserFoundException(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> new ItemFoundException(itemId));
        Item updateItem;
        if (itemToUpdate.getOwner().getId().equals(userId)) {
            updateItem = itemService.composeItem(itemToUpdate, itemDto);
            itemRepository.update(updateItem.getOwner(), updateItem.getName(), updateItem.getDescription(),
                    updateItem.getAvailable(), updateItem.getId());
        } else {
            throw new UserMismatchException(userId, itemId);
        }
        return itemMapper.toDTO(updateItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemFoundException(itemId));
        if (item.getOwner().getId().equals(userId)) {
            return itemService.connectComment(itemService.connectBooking(itemMapper.toDTO(item)));
        }
        return itemService.connectComment(itemMapper.toDTO(item));
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUserWithId(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId) {
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
                                 @NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        Comment comment;
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItem_IdAndBooker_Id(itemId, userId);
        if (!bookings.isEmpty() && bookings.stream()
                .anyMatch(x -> !x.getStatus().equals(Status.REJECTED) && x.getStart().isBefore(now))) {
            comment = Comment.builder()
                    .text(commentDto.getText())
                    .author(bookings.get(0).getBooker())
                    .item(bookings.get(0).getItem())
                    .created(LocalDateTime.now())
                    .build();
            commentRepository.save(comment);
        } else {
            throw new UnsupportedStatusException("User with id-" + userId + " can't comment item with id -" + itemId);
        }
        return commentMapper.toDTO(comment);
    }
}
