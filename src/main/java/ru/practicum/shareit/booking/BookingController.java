package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingFoundException(bookingId));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toDTO(booking);
        } else {
            throw new BookingFoundException(bookingId);
        }
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsOfUser(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state) {
        userRepository.findById(userId).orElseThrow(() -> new UserFoundException(userId));
        List<Booking> allUserBookings = bookingRepository.findAllByBooker_Id(userId);
        return bookingService.getUserBookings(state, allUserBookings);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookingsOfOwner(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL", required = false) String state) {
        userRepository.findById(userId).orElseThrow(() -> new UserFoundException(userId));
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        if (userItems.isEmpty()) {
            throw new NotFoundException("User isn't owner any item");
        }
        List<Booking> allBookings = bookingRepository.findAllByBooker_IdNotAndItemIn(userId, userItems);
        return bookingService.getUserBookings(state, allBookings);
    }

    @PostMapping
    public BookingDto addBooking(@NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date of booking after end date");
        }
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserFoundException(userId));
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new ItemFoundException(bookingDtoIn.getItemId()));
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Owner can't booking own item");
        }
        Booking booking = bookingService.newBooking(bookingDtoIn, booker, item);
        if (item.getAvailable()) {
            return bookingMapper.toDTO(bookingRepository.save(booking));
        } else {
            throw new ItemUnavailableException(item.getId());
        }
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @NotEmpty @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @NotEmpty @RequestParam Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingFoundException(bookingId));
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
            throw new NotFoundException("User with id -" + userId + " isn't owner of item with id - " + itemId);
        }
        return bookingMapper.toDTO(booking);
    }
}
