package ru.practicum.shareit.exception;

public class UserFoundException extends NotFoundException {
    public UserFoundException(Long userId) {
        super(String.format("User with id - %d not found", userId));
    }
}
