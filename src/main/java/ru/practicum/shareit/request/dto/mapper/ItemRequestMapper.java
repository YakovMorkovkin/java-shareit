package ru.practicum.shareit.request.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.mapper.UserMapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    @Mapping(target = "requestorId", source = "model.requestor.id")
    ItemRequestDto toDTO(ItemRequest model);

    ItemRequest toModel(ItemRequestDto dto);
}