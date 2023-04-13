package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Service
@Validated
public interface BookingService {
    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsOfUser(String state, Long userId, Integer offset, Integer limit);

    List<BookingDto> getAllItemsBookingsOfOwner(Long userId, String state, Integer offset, Integer limit);

    BookingDto addBooking(Long userId, BookingDtoIn bookingDtoIn);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    Booking getBooking(Long bookingId);
}
