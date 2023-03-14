package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Service
@Validated
public interface BookingService {
    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsOfUser(String state, Long userId, @Min(0) Integer offset, @Min(1) @Max(50) Integer limit);

    List<BookingDto> getAllItemsBookingsOfOwner(Long userId, String state, @Min(0) Integer offset, @Min(1) @Max(50) Integer limit);

    BookingDto addBooking(Long userId, BookingDtoIn bookingDtoIn);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    Booking getBooking(Long bookingId);
}
