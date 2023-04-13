package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.Constants.ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsOfUser(@RequestHeader(ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                 @RequestParam(value = "from", defaultValue = "0",
                                                         required = false) Integer offset,
                                                 @RequestParam(value = "size", defaultValue = "10",
                                                         required = false) Integer limit) {
        return bookingService.getAllBookingsOfUser(state, userId, offset, limit);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookingsOfOwner(@RequestHeader(ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL", required = false) String state,
                                                       @RequestParam(value = "from", defaultValue = "0",
                                                               required = false) Integer offset,
                                                       @RequestParam(value = "size", defaultValue = "10",
                                                               required = false) Integer limit) {
        return bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(ID) Long userId,
                                 @RequestBody BookingDtoIn bookingDtoIn) {
        return bookingService.addBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestHeader(ID) Long userId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }
}
