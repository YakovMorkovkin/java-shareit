package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.mapper.BookingShortMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.UserMismatchException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.REJECTED;

@Service
@RequiredArgsConstructor
@Primary
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingShortMapper bookingShortMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item newItem = itemMapper.toModel(itemDto);
        newItem.setOwner(userService.getUser(userId));
        if (itemDto.getRequestId() != null) {
            newItem.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElse(null));
        }
        return itemMapper.toDTO(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item itemToUpdate = getItem(itemId);
        Item updateItem;
        if (itemToUpdate.getOwner().getId().equals(userId)) {
            updateItem = composeItem(itemToUpdate, itemDto);
            itemRepository.update(updateItem.getOwner(), updateItem.getName(), updateItem.getDescription(),
                    updateItem.getAvailable(), updateItem.getId());
        } else {
            throw new UserMismatchException(userId, itemId);
        }
        return itemMapper.toDTO(updateItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = getItem(itemId);
        if (item.getOwner().getId().equals(userId)) {
            return connectComment(connectBooking(itemMapper.toDTO(item)));
        }
        return connectComment(itemMapper.toDTO(item));
    }

    @Override
    public List<ItemDto> getAllItemsOfUserWithId(Long userId, Integer offset, Integer limit) {
        return itemRepository.findByOwnerId(userId, PageRequest.of(offset, limit, Sort.by("id").ascending()))
                .map(itemMapper::toDTO)
                .map(this::connectBooking)
                .map(this::connectComment)
                .getContent();
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer offset, Integer limit) {
        if (text != null && !text.isEmpty()) {
            return itemRepository.findByAvailableIsTrueAndDescriptionContainsIgnoreCaseOrNameContainsIgnoreCase(text,
                            text,
                            PageRequest.of(offset, limit, Sort.by("id").ascending()))
                    .map(itemMapper::toDTO).getContent();
        } else return new ArrayList<>();
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Comment comment;
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerId(itemId, userId);
        if (!bookings.isEmpty() && bookings.stream()
                .anyMatch(x -> !x.getStatus().equals(REJECTED) && x.getStart().isBefore(now))) {
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

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private Item composeItem(Item item, ItemDto itemDto) {
        return Item.builder()
                .id(item.getId())
                .name(itemDto.getName() != null ?
                        itemDto.getName() : item.getName())
                .owner(item.getOwner())
                .description(itemDto.getDescription() != null ?
                        itemDto.getDescription() : item.getDescription())
                .available(itemDto.getAvailable() != null ?
                        itemDto.getAvailable() : item.getAvailable())
                .build();
    }

    @Override
    public ItemDto connectBooking(ItemDto itemDto) {
        itemDto.setLastBooking(getLastAndNextItemBookings(itemDto).get(0));
        itemDto.setNextBooking(getLastAndNextItemBookings(itemDto).get(1));
        return itemDto;
    }

    @Override
    public ItemDto connectComment(ItemDto itemDto) {
        itemDto.setComments(getItemComments(itemDto));
        return itemDto;
    }

    private List<BookingShortDto> getLastAndNextItemBookings(ItemDto itemDto) {
        List<BookingShortDto> lastAndNextBookings = new ArrayList<>();
        lastAndNextBookings.add(0, null);
        lastAndNextBookings.add(1, null);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> itemBookings = bookingRepository.findAllByItemId(itemDto.getId());
        lastAndNextBookings.add(0, bookingShortMapper.toDTO(itemBookings.stream()
                .filter(x -> !x.getStatus().equals(REJECTED))
                .filter(x -> x.getEnd().isBefore(now) || (x.getStart().isBefore(now) && x.getEnd().isAfter(now)))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .findFirst().orElse(null)));
        lastAndNextBookings.add(1, bookingShortMapper.toDTO(itemBookings.stream()
                .filter(x -> !x.getStatus().equals(REJECTED))
                .filter(x -> x.getStart().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart))
                .findFirst().orElse(null)));
        return lastAndNextBookings;
    }

    private List<CommentDto> getItemComments(ItemDto itemDto) {
        return commentRepository.findAllByItem_Id(itemDto.getId()).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
