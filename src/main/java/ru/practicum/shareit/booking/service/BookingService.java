package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface BookingService {
    Booking newBooking(BookingDtoIn bookingDtoIn, User booker, Item item);

    List<BookingDto> getUserBookings(String state, List<Booking> allUserBookings);
}
