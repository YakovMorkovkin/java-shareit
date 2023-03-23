package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.ID;

@WebMvcTest(BookingController.class)
class BookingControllerITests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void getBookingById() {
        Long userId = 1L;
        Long bookingId = 0L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(ID, userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getBookingById(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getAllBookingsOfUser() {
        Long userId = 1L;
        String state = "ALL";
        Integer offset = 0;
        Integer limit = 10;

        mockMvc.perform(get("/bookings")
                        .header(ID, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsOfUser(state, userId, offset, limit);
    }

    @SneakyThrows
    @Test
    void getAllItemsBookingsOfOwner() {

        Long userId = 1L;
        String state = "ALL";
        Integer offset = 0;
        Integer limit = 10;

        mockMvc.perform(get("/bookings/owner")
                        .header(ID, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllItemsBookingsOfOwner(userId, state, offset, limit);
    }

    @SneakyThrows
    @Test
    void addBooking_whenBookingIsCorrect_thenStatusIsOk() {
        Long userId = 1L;
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).addBooking(userId, bookingDtoIn);
    }

    @SneakyThrows
    @Test
    void approveBooking() {

        Long userId = 1L;
        Long bookingId = 0L;
        Boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(ID, userId)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).approveBooking(bookingId, userId, approved);
    }
}