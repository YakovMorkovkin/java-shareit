package ru.practicum.shareit.booking.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class, Status.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    BookingDto toDTO(Booking model);

    Booking toModel(BookingDto bookingDto);
}