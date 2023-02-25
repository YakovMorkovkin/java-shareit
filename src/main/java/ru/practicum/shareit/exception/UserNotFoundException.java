package ru.practicum.shareit.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long userId) {
        super(String.format("User with id - %d not found", userId));
    }
}
