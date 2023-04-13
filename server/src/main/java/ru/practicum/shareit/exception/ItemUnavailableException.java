package ru.practicum.shareit.exception;

public class ItemUnavailableException extends UnavailableException {
    public ItemUnavailableException(Long itemId) {
        super(String.format("Item with id - %d unavailable", itemId));
    }
}
