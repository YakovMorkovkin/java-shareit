package ru.practicum.shareit.item.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {
    @Mapping(target = "authorName", source = "model.author.name")
    CommentDto toDTO(Comment model);
}
