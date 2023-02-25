package ru.practicum.shareit.exception;

public class BookingUnavailableException extends NotFoundException {
    public BookingUnavailableException(String message) {
        super(message);
    }
}
