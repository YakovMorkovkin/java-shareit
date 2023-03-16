package ru.practicum.shareit.booking.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class BookingShortMapperImplUnitTests {
    @Autowired
    BookingShortMapperImpl bookingMapper;

    User testBooker = User.builder()
            .id(3L)
            .name("Test2")
            .email("test2@email.ru")
            .build();

    Booking testBooking = Booking.builder()
            .id(1L)
            .booker(testBooker)
            .build();

    BookingShortDto testBookingShortDto = BookingShortDto.builder()
            .id(1L)
            .bookerId(testBooker.getId())
            .build();

    @Test
    void toDTO_whenBookingNotNull_thenReturnBookingShortDto() {
        BookingShortDto actualBookingShortDto = bookingMapper.toDTO(testBooking);
        assertEquals(testBookingShortDto.getId(), actualBookingShortDto.getId());
        assertEquals(testBookingShortDto.getBookerId(), actualBookingShortDto.getBookerId());
    }

    @Test
    void toDTO_whenBookingNull_thenReturnNull() {
        BookingShortDto actualBookingShortDto = bookingMapper.toDTO(null);
        assertNull(actualBookingShortDto);
    }
}