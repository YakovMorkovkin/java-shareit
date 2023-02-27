package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends UnavailableException {
    public UnsupportedStatusException(String status) {
        super(String.format("Unknown state: %s", status));
    }
}
