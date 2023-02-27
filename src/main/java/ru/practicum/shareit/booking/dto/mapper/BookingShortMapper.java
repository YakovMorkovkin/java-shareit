package ru.practicum.shareit.booking.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingShortMapper {
    @Mapping(target = "bookerId", source = "model.booker.id")
    BookingShortDto toDTO(Booking model);
}
