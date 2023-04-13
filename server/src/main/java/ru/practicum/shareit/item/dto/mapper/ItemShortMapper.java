package ru.practicum.shareit.item.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.mapper.BookingShortMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.mapper.UserMapper;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, ItemRequest.class, CommentMapper.class, BookingShortMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemShortMapper {
    @Mapping(target = "ownerId", source = "model.owner.id")
    @Mapping(target = "requestId", source = "model.itemRequest.id")
    ItemShortDto toDTO(Item model);
}