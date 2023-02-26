package ru.practicum.shareit.exception;

public class EndBeforeStartException extends UnavailableException {
    public EndBeforeStartException() {
        super("Start date of booking after end date");
    }
}
