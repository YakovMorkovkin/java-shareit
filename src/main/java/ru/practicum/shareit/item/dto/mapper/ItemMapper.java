package ru.practicum.shareit.item.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.mapper.BookingShortMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemRequest.class, CommentMapper.class, BookingShortMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {
    ItemDto toDTO(Item model);

    Item toModel(ItemDto dto);
}
