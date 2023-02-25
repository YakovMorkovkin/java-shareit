package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toDTO(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsOfUser(Long userId, String state) {
        userService.getUser(userId);
        List<Booking> allUserBookings = bookingRepository.findAllByBooker_Id(userId);
        return getUserBookings(state, allUserBookings);
    }

    @Override
    public List<BookingDto> getAllItemsBookingsOfOwner(Long userId, String state) {
        userService.getUser(userId);
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        if (userItems.isEmpty()) {
            throw new NotFoundException("User isn't owner any item");
        }
        List<Booking> allBookings = bookingRepository.findAllByBooker_IdNotAndItemIn(userId, userItems);
        return getUserBookings(state, allBookings);
    }

    @Override
    public BookingDto addBooking(Long userId, BookingDtoIn bookingDtoIn) {
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date of booking after end date");
        }
        User booker = userService.getUser(userId);
        Item item = itemService.getItem(bookingDtoIn.getItemId());
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new BookingUnavailableException("Owner can't booking own item");
        }
        Booking booking = newBooking(bookingDtoIn, booker, item);
        if (item.getAvailable()) {
            return bookingMapper.toDTO(bookingRepository.save(booking));
        } else {
            throw new ItemUnavailableException(item.getId());
        }
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        Long itemId = booking.getItem().getId();
        if (booking.getItem().getOwner().getId().equals(userId) && booking.getStatus().equals(Status.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking with id-" + booking.getId()
                    + " is already approved");
        } else if (booking.getItem().getOwner().getId().equals(userId) && approved) {
            booking.setStatus(Status.APPROVED);
            bookingRepository.update(booking.getStatus(), bookingId);
        } else if (booking.getItem().getOwner().getId().equals(userId) && !approved) {
            booking.setStatus(Status.REJECTED);
            bookingRepository.update(booking.getStatus(), bookingId);
        } else {
            throw new BookingUnavailableException("User with id -" + userId + " isn't owner of item with id - " + itemId);
        }
        return bookingMapper.toDTO(booking);
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    private Booking newBooking(BookingDtoIn bookingDtoIn, User booker, Item item) {
        return Booking.builder()
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    private List<BookingDto> getUserBookings(String state, List<Booking> allUserBookings) {
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
