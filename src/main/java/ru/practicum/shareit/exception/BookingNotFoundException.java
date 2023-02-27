package ru.practicum.shareit.exception;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException(Long bookingId) {
        super(String.format("Booking with id - %d not found", bookingId));
    }
}
