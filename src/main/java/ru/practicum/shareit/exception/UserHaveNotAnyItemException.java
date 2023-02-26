package ru.practicum.shareit.exception;

public class UserHaveNotAnyItemException extends NotFoundException {
    public UserHaveNotAnyItemException() {
        super("User isn't owner any item");
    }
}
