package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;

    @Override
    public Booking newBooking(BookingDtoIn bookingDtoIn, User booker, Item item) {
        return Booking.builder()
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    @Override
    public List<BookingDto> getUserBookings(String state, List<Booking> allUserBookings) {
        LocalDateTime now = LocalDateTime.now();
        List<BookingDto> result;
        switch (state) {
            case "ALL":
                result = allUserBookings.stream()
                        .map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
                result.sort(Comparator.comparing(BookingDto::getId).reversed());
                return result;
            case "CURRENT":
                result = allUserBookings.stream()
                        .filter(x -> x.getStart().isBefore(now) && x.getEnd().isAfter(now))
                        .map(bookingMapper::toDTO)
                        .collect(Collectors.toList());
                result.sort(Comparator.comparing(BookingDto::getId).reversed());
                return result;
            case "PAST":
                result = allUserBookings.stream()
                        .filter(x -> x.getStart().isBefore(now) && x.getEnd().isBefore(now))
                        .map(bookingMapper::toDTO)
                        .sorted(Comparator.comparing(BookingDto::getId).reversed())
                        .collect(Collectors.toList());
                result.sort(Comparator.comparing(BookingDto::getId).reversed());
                return result;
            case "FUTURE":
                result = allUserBookings.stream()
                        .filter(x -> x.getStart().isAfter(now) && x.getEnd().isAfter(now))
                        .map(bookingMapper::toDTO)
                        .sorted(Comparator.comparing(BookingDto::getId).reversed())
                        .collect(Collectors.toList());
                result.sort(Comparator.comparing(BookingDto::getId).reversed());
                return result;
            case "WAITING":
                result = allUserBookings.stream()
                        .filter(x -> x.getStatus().equals(Status.WAITING))
                        .map(bookingMapper::toDTO)
                        .sorted(Comparator.comparing(BookingDto::getId).reversed())
                        .collect(Collectors.toList());
                result.sort(Comparator.comparing(BookingDto::getId).reversed());
                return result;
            case "REJECTED":
                result = allUserBookings.stream()
                        .filter(x -> x.getStatus().equals(Status.REJECTED))
                        .map(bookingMapper::toDTO)
                        .sorted(Comparator.comparing(BookingDto::getId).reversed())
                        .collect(Collectors.toList());
                result.sort(Comparator.comparing(BookingDto::getId).reversed());
                return result;
            default:
                throw new UnsupportedStatusException(state);
        }
    }
}
