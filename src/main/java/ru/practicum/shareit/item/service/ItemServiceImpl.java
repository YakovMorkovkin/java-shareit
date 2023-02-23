package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.mapper.BookingShortMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final BookingShortMapper bookingShortMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Item composeItem(Item item, ItemDto itemDto) {
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
        List<BookingShortDto> lastAndNextBokings = new ArrayList<>();
        lastAndNextBokings.add(0, null);
        lastAndNextBokings.add(1, null);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> itemBookings = bookingRepository.findAllByItem_Id(itemDto.getId());
        if (!itemBookings.isEmpty()) {
            itemBookings.sort(Comparator.comparing(Booking::getId));
            for (int i = 1; i < (itemBookings.size()); i++) {
                if (itemBookings.get(i).getStart().isAfter(now) && i >= 1) {
                    lastAndNextBokings.add(1, bookingShortMapper.toDTO(itemBookings.get(i)));
                    lastAndNextBokings.add(0, bookingShortMapper.toDTO(itemBookings.get(i - 1)));
                    if (itemBookings.get(i).getStart().isAfter(now)) {
                        lastAndNextBokings.add(1, bookingShortMapper.toDTO(itemBookings.get(i)));
                    } else if (itemBookings.get(i).getStart().isBefore(now)) {
                        lastAndNextBokings.add(0, bookingShortMapper.toDTO(itemBookings.get(i)));
                    }
                }
            }
        }
        return lastAndNextBokings;
    }

    private List<CommentDto> getItemComments(ItemDto itemDto) {
        return commentRepository.findAllByItem_Id(itemDto.getId()).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
