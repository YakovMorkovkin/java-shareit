package ru.practicum.shareit.exception;

import java.time.LocalDateTime;

public class StartEndMismatchException extends UnavailableException {
    public StartEndMismatchException(LocalDateTime date) {
        super("Start or End is null or start date of booking after end date - " + date);
    }
}
