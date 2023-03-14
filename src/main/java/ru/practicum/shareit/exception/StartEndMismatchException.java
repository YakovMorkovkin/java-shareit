package ru.practicum.shareit.exception;

public class StartEndMismatchException extends UnavailableException {
    public StartEndMismatchException() {
        super("Start date of booking after end date");
    }
}
