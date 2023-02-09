package ru.practicum.shareit.exception;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException(String email) {
        super(String.format("User with email - %s already exists", email));
    }
}
