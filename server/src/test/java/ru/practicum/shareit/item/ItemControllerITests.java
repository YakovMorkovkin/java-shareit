package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.ID;

@WebMvcTest(ItemController.class)
class ItemControllerITests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void addItem() {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("test item")
                .description("test description")
                .available(true)
                .build();
        when(itemService.addItem(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("test item")
                .description("test description")
                .available(true)
                .build();
        when(itemService.updateItem(itemId, userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        Long userId = 1L;
        Long itemId = 0L;

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(ID, userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getItemById(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getAllItemsOfUserWithId() {
        Long userId = 1L;
        Integer offset = 0;
        Integer limit = 10;
        mockMvc.perform(get("/items").header(ID, userId).param("from", "0").param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getAllItemsOfUserWithId(userId, offset, limit);
    }

    @SneakyThrows
    @Test
    void searchItem() {
        Integer offset = 0;
        Integer limit = 10;
        String text = "test";
        mockMvc.perform(get("/items/search").param("text", "test").param("from", "0").param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).searchItem(text, offset, limit);
    }

    @SneakyThrows
    @Test
    void addComment() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("test comment")
                .created(LocalDateTime.now())
                .build();
        when(itemService.addComment(itemId, userId, commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }
}