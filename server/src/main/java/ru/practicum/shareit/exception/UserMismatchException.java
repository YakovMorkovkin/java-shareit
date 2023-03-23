package ru.practicum.shareit.exception;

public class UserMismatchException extends RuntimeException {

    public UserMismatchException(Long userId, Long itemId) {
        super(String.format("User with id - %d isn't owner item with id - %d", userId, itemId));
    }
}
