package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryITests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    User owner = User.builder()
            .id(1L)
            .name("Item owner")
            .email("item_owner@test.ru")
            .build();
    User requestor = User.builder()
            .id(2L)
            .name("Item requestor")
            .email("item_requestor@test.ru")
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requestor(requestor)
            .description("item request")
            .created(LocalDateTime.now())
            .build();
    Item item = Item.builder()
            .id(1L)
            .owner(owner)
            .description("test item")
            .available(true)
            .name("item")
            .build();

    Item item1 = Item.builder()
            .id(2L)
            .owner(owner)
            .description("test item1")
            .available(true)
            .name("item1")
            .itemRequest(itemRequest)
            .build();

    @BeforeAll
    public void addUsers() {
        userRepository.save(owner);
        userRepository.save(requestor);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
        itemRepository.save(item1);
    }

    @Test
    void update() {
        User owner = requestor;
        String name = "updated name";
        String description = "updated description";
        Boolean available = false;
        itemRepository.update(owner, name, description, available, 2L);
        Item updatedItem = itemRepository.findById(2L).get();
        assertEquals(owner.getId(), updatedItem.getOwner().getId());
        assertEquals(name, updatedItem.getName());
        assertEquals(description, updatedItem.getDescription());
        assertEquals(available, updatedItem.getAvailable());
    }

    @Test
    void findByOwnerId() {
    }

    @Test
    void testFindByOwnerId() {
    }

    @Test
    void findByAvailableIsTrueAndDescriptionContainsIgnoreCaseOrNameContainsIgnoreCase() {
    }

    @Test
    void findAllByItemRequestId() {
    }
}