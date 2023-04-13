package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Getting booking with id-{} dy user with id-{}", bookingId, userId);
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toDTO(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsOfUser(String state, Long userId, Integer offset, Integer limit) {
        userService.getUser(userId);
        log.info("Get all bookings of user with id-{} with state-{}", userId, state);
        return getUserBookings(state, userId, offset, limit);
    }

    @Override
    public List<BookingDto> getAllItemsBookingsOfOwner(Long userId, String state, Integer offset, Integer limit) {
        userService.getUser(userId);
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        if (userItems.isEmpty()) {
            throw new UserHaveNotAnyItemException();
        }
        log.info("Get all items bookings of owner with id-{} with state-{}", userId, state);
        return getBookingsForOwner(state, userItems, userId, offset, limit);
    }

    @Override
    public BookingDto addBooking(Long userId, BookingDtoIn bookingDtoIn) {
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart()) ||
                bookingDtoIn.getEnd().equals(bookingDtoIn.getStart()) ||
                bookingDtoIn.getStart() == null) {
            throw new StartEndMismatchException(bookingDtoIn.getStart());
        }
        User booker = userService.getUser(userId);
        Item item = itemService.getItem(bookingDtoIn.getItemId());
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new BookingUnavailableException("Owner can't booking own item");
        }
        Booking booking = newBooking(bookingDtoIn, booker, item);
        if (item.getAvailable()) {
            log.info("Add booking by user with id-{}", userId);
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
            throw new DoubleApprovingException(booking.getId());
        } else if (booking.getItem().getOwner().getId().equals(userId) && approved) {
            booking.setStatus(Status.APPROVED);
            bookingRepository.update(booking.getStatus(), bookingId);
        } else if (booking.getItem().getOwner().getId().equals(userId) && !approved) {
            booking.setStatus(Status.REJECTED);
            bookingRepository.update(booking.getStatus(), bookingId);
        } else {
            throw new BookingUnavailableException("User with id -" + userId + " isn't owner of item with id - " + itemId);
        }
        log.info("Booking with id-{} now has state state-{}", userId, booking.getStatus());
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

    private List<BookingDto> getUserBookings(String state, Long userId, Integer offset, Integer limit) {
        LocalDateTime now = LocalDateTime.now();
        int page = 0;
        if (((offset + 1) / limit) > 0 && ((offset + 1) % limit) == 0) {
            page = ((offset + 1) / limit) - 1;
        } else if (((offset + 1) / limit) > 0 && ((offset + 1) % limit) != 0) {
            page = ((offset + 1) / limit);
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerId(userId, PageRequest.of(page, limit, Sort.by("id")
                                .descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now,
                                PageRequest.of(page, limit, Sort.by("id").ascending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "PAST":
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBefore(userId, now, now,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfter(userId, now, now,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    private List<BookingDto> getBookingsForOwner(String state, List<Item> ownerItems,
                                                 Long userId, Integer offset, Integer limit) {
        LocalDateTime now = LocalDateTime.now();
        int page = 0;
        if (((offset + 1) / limit) > 0 && ((offset + 1) % limit) == 0) {
            page = ((offset + 1) / limit) - 1;
        } else if (((offset + 1) / limit) > 0 && ((offset + 1) % limit) != 0) {
            page = ((offset + 1) / limit);
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdNotAndItemIn(userId, ownerItems,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "CURRENT":
                return bookingRepository.findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsAfter(userId, ownerItems,
                                now, now, PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "PAST":
                return bookingRepository.findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsBefore(userId, ownerItems,
                                now, now, PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "FUTURE":
                return bookingRepository.findAllByBookerIdNotAndItemInAndStartIsAfterAndEndIsAfter(userId, ownerItems,
                                now, now, PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "WAITING":
                return bookingRepository.findAllByBookerIdNotAndItemInAndStatus(userId, ownerItems, Status.WAITING,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            case "REJECTED":
                return bookingRepository.findAllByBookerIdNotAndItemInAndStatus(userId, ownerItems, Status.REJECTED,
                                PageRequest.of(page, limit, Sort.by("id").descending()))
                        .map(bookingMapper::toDTO)
                        .getContent();
            default:
                throw new UnsupportedStatusException(state);
        }
    }
}
