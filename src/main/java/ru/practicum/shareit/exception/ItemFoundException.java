package ru.practicum.shareit.exception;

public class ItemFoundException extends NotFoundException {
    public ItemFoundException(Long itemId) {
        super(String.format("Item with id - %d not found", itemId));
    }
}

