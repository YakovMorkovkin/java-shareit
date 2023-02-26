package ru.practicum.shareit.exception;

public class DoubleApprovingException extends UnavailableException {
    public DoubleApprovingException(Long bookingId) {
        super(String.format("Booking with id-%d is already approved", bookingId));
    }
}
