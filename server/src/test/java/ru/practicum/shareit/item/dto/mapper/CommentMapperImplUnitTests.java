package ru.practicum.shareit.item.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CommentMapperImplUnitTests {

    @Autowired
    CommentMapperImpl commentMapper;
    LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 1);
    User testAuthor = User.builder()
            .id(1L)
            .name("Test")
            .email("test@email.ru")
            .build();
    User testUser = User.builder()
            .id(2L)
            .name("Test1")
            .email("test1@email.ru")
            .build();
    Item testItem = Item.builder()
            .id(1L)
            .name("Item")
            .owner(testUser)
            .description("test Item")
            .available(true)
            .build();
    Comment testComment = Comment.builder()
            .id(1L)
            .author(testAuthor)
            .item(testItem)
            .text("test text")
            .created(dateTime)
            .build();

    CommentDto testCommentDto = CommentDto.builder()
            .id(1L)
            .authorName("Test")
            .text("test text")
            .created(dateTime)
            .build();

    @Test
    void toDTO_whenCommentNotNull_thenReturnCommentDto() {
        CommentDto actualCommentDto = commentMapper.toDTO(testComment);
        assertEquals(testCommentDto.getId(), actualCommentDto.getId());
        assertEquals(testCommentDto.getAuthorName(), actualCommentDto.getAuthorName());
        assertEquals(testCommentDto.getText(), actualCommentDto.getText());
        assertEquals(testCommentDto.getCreated(), actualCommentDto.getCreated());
    }

    @Test
    void toDTO_whenCommentNull_thenReturnNull() {
        CommentDto actualCommentDto = commentMapper.toDTO(null);
        assertNull(actualCommentDto);
    }
}