package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.ID;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerITests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void addItemRequest() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .requestorId(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.addItemRequest(userId, itemRequestDto)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header(ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenItemRequestIsNotValid_thenStatusIsBadRequest() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .requestorId(1L)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.addItemRequest(userId, itemRequestDto)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(itemRequestService, never()).addItemRequest(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void getAllOwnItemRequests() {
        Long userId = 1L;
        mockMvc.perform(get("/requests").header(ID, userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService).getAllOwnItemRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsOfUsers() {
        Long userId = 1L;
        Integer offset = 0;
        Integer limit = 10;
        mockMvc.perform(get("/requests/all").header(ID, userId).param("from", "0").param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService).getAllItemRequestsOfUsers(userId, offset, limit);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        Long userId = 1L;
        Long requestId = 0L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(ID, userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService).getItemRequestById(userId, requestId);
    }
}