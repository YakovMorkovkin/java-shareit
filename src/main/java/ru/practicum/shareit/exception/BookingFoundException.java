package ru.practicum.shareit.exception;

public class BookingFoundException extends NotFoundException {
    public BookingFoundException(Long bookingId) {
        super(String.format("Booking with id - %d not found", bookingId));
    }
}
