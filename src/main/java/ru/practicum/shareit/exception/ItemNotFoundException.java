package ru.practicum.shareit.exception;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(Long itemId) {
        super(String.format("Item with id - %d not found", itemId));
    }
}

