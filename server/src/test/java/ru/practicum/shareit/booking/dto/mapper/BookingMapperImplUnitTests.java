package ru.practicum.shareit.booking.dto.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class BookingMapperImplUnitTests {

    @Autowired
    BookingMapperImpl bookingMapper;
    LocalDateTime start = LocalDateTime.of(2023, 11, 1, 12, 1);
    LocalDateTime end = LocalDateTime.of(2023, 12, 1, 12, 1);
    LocalDateTime dateTime = LocalDateTime.of(2024, 12, 1, 12, 1);

    User testOwner = User.builder()
            .id(1L)
            .name("Test")
            .email("test@email.ru")
            .build();

    User testRequestor = User.builder()
            .id(2L)
            .name("Test1")
            .email("test1@email.ru")
            .build();

    User testBooker = User.builder()
            .id(3L)
            .name("Test2")
            .email("test2@email.ru")
            .build();
    UserDto testBookerDto = UserDto.builder()
            .id(3L)
            .name("Test2")
            .email("test2@email.ru")
            .build();

    ItemRequest testItemRequest = ItemRequest.builder()
            .id(1L)
            .description("test request")
            .requestor(testRequestor)
            .created(dateTime)
            .build();

    Item testItem = Item.builder()
            .id(1L)
            .name("Item")
            .owner(testOwner)
            .description("test Item")
            .available(true)
            .itemRequest(testItemRequest)
            .build();
    ItemDto testItemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("test Item")
            .available(true)
            .requestId(1L)
            .build();

    Booking testBooking = Booking.builder()
            .id(1L)
            .item(testItem)
            .booker(testBooker)
            .status(Status.WAITING)
            .start(start)
            .end(end)
            .build();

    BookingDto testBookingDto = BookingDto.builder()
            .id(1L)
            .item(testItemDto)
            .booker(testBookerDto)
            .status(Status.WAITING)
            .start(start)
            .end(end)
            .build();

    @Test
    void toDTO_whenBookingNotNull_thenReturnBookingDto() {
        BookingDto actualBookingDto = bookingMapper.toDTO(testBooking);
        assertEquals(testBookingDto.getId(), actualBookingDto.getId());
        assertEquals(testBookingDto.getItem(), actualBookingDto.getItem());
        assertEquals(testBookingDto.getBooker(), actualBookingDto.getBooker());
        assertEquals(testBookingDto.getStatus(), actualBookingDto.getStatus());
        assertEquals(testBookingDto.getStart(), actualBookingDto.getStart());
        assertEquals(testBookingDto.getEnd(), actualBookingDto.getEnd());
    }

    @Test
    void toDTO_whenBookingNull_thenReturnNull() {
        BookingDto actualBookingDto = bookingMapper.toDTO(null);
        assertNull(actualBookingDto);
    }

    @Test
    void toModel_whenBookingDtoNotNull_thenReturnBooking() {
        Booking actualBooking = bookingMapper.toModel(testBookingDto);
        assertEquals(testBooking.getId(), actualBooking.getId());
        assertEquals(testBooking.getItem().getId(), actualBooking.getItem().getId());
        assertEquals(testBooking.getBooker().getId(), actualBooking.getBooker().getId());
        assertEquals(testBooking.getStatus(), actualBooking.getStatus());
        assertEquals(testBooking.getStart(), actualBooking.getStart());
        assertEquals(testBooking.getEnd(), actualBooking.getEnd());
    }

    @Test
    void toModel_whenBookingDtoNull_thenReturnNull() {
        Booking actualBooking = bookingMapper.toModel(null);
        assertNull(actualBooking);
    }
}