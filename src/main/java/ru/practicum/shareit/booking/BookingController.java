package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static ru.practicum.shareit.Constants.ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @NotEmpty @RequestHeader(ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsOfUser(@NotEmpty @RequestHeader(ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookingsOfOwner(@NotEmpty @RequestHeader(ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllItemsBookingsOfOwner(userId, state);
    }

    @PostMapping
    public BookingDto addBooking(@NotEmpty @RequestHeader(ID) Long userId,
                                 @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        return bookingService.addBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @NotEmpty @RequestHeader(ID) Long userId,
                                     @NotEmpty @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }
}
