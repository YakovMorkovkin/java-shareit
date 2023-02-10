package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dao.ItemRepositoryImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserRepositoryImpl;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class ShareItTests {

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemService itemService;


    @BeforeEach
    public void beforeEach() {
        userRepository = new UserRepositoryImpl();
        itemRepository = new ItemRepositoryImpl(userRepository);
        itemService = new ItemServiceImpl(itemRepository, userRepository);
    }

    User user1 = User.builder()
            .id(1L)
            .name("Mike")
            .email("email@mail.ru")
            .build();

    User user1Upd = User.builder()
            .name("Nick")
            .email("updatedEmail@mail.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("Nick")
            .email("email1@mail.ru")
            .build();

    Item item = Item.builder()
            .name("Spoon")
            .description("Silver spoon")
            .available(true)
            .build();

    Item itemUpd = Item.builder()
            .id(1L)
            .owner(user1)
            .name("Updated Spoon")
            .description("Updated Silver spoon")
            .available(true)
            .build();
    Item item1 = Item.builder()
            .id(2L)
            .name("Fork")
            .description("Bronze fork")
            .available(true)
            .build();

    Item item2 = Item.builder()
            .id(3L)
            .name("Knife")
            .description("Silver knife")
            .available(true)
            .build();


    @Test
    void contextLoads() {
    }

    //User Tests
    @Test
    void testAddUser() {
        log.debug("user add test");
        User testUser = userRepository.addUser(user1);
        assertThat(testUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Mike")
                .hasFieldOrPropertyWithValue("email", "email@mail.ru");
        log.debug("{}", testUser);
    }

    @Test
    void testUpdateUser() {
        log.debug("user update test");
        log.debug("{}", userRepository.addUser(user1));

        User updatedUser = userRepository.updateUser(1L, user1Upd);
        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Nick")
                .hasFieldOrPropertyWithValue("email", "updatedEmail@mail.ru");
        log.debug("{}", updatedUser);
    }

    @Test
    void testGetUserByID() {
        log.debug("user get by id test");
        log.debug("{}", userRepository.addUser(user1));
        log.debug("{}", userRepository.addUser(user2));
        User testUser = userRepository.getUserById(2L).get();
        assertThat(testUser)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Nick")
                .hasFieldOrPropertyWithValue("email", "email1@mail.ru");
        log.debug("{}", testUser);
    }

    @Test
    void testGetAllUsers() {
        log.debug("get all users test");
        log.debug("{}", userRepository.addUser(user1));
        log.debug("{}", userRepository.addUser(user2));
        assertEquals(2, userRepository.getAllUsers().size());
    }

    @Test
    void testDeleteUser() {
        log.debug("user delete test");
        log.debug("{}", userRepository.addUser(user1));
        log.debug("{}", userRepository.addUser(user2));
        assertEquals(2, userRepository.getAllUsers().size());
        userRepository.deleteUser(1L);
        assertEquals(1, userRepository.getAllUsers().size());
    }

    //Item Tests

    @Test
    void testAddItem() {
        log.debug("item add test");
        userRepository.addUser(user1);
        Item testItem = itemRepository.addItem(1L, item);
        assertThat(testItem)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("owner", user1)
                .hasFieldOrPropertyWithValue("name", "Spoon")
                .hasFieldOrPropertyWithValue("description", "Silver spoon")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("request", null);
        log.debug("{}", testItem);
    }

    @Test
    void testUpdateItem() {
        log.debug("item update test");
        userRepository.addUser(user1);
        itemRepository.addItem(1L, item);
        Item testItem = itemRepository.updateItem(1L, 1L, itemUpd);
        assertThat(testItem)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("owner", user1)
                .hasFieldOrPropertyWithValue("name", "Updated Spoon")
                .hasFieldOrPropertyWithValue("description", "Updated Silver spoon")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("request", null);
        log.debug("{}", testItem);
    }

    @Test
    void testGetItemByID() {
        log.debug("item get by id test");
        userRepository.addUser(user1);
        itemRepository.addItem(1L, item);
        itemRepository.addItem(1L, item1);
        Item testItem = itemRepository.getItemById(2L).get();
        assertThat(testItem)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("owner", user1)
                .hasFieldOrPropertyWithValue("name", "Fork")
                .hasFieldOrPropertyWithValue("description", "Bronze fork")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("request", null);
        log.debug("{}", testItem);
    }

    @Test
    void testGetAllItems() {
        log.debug("get all items test");
        userRepository.addUser(user1);
        itemRepository.addItem(1L, item);
        itemRepository.addItem(1L, item1);
        assertEquals(2, itemRepository.getAllItems().size());
    }

    @Test
    void testSearchItemBySubstring() {
        log.debug("search items test");
        userRepository.addUser(user1);
        itemRepository.addItem(1L, item);
        itemRepository.addItem(1L, item1);
        itemRepository.addItem(1L, item2);
        assertEquals(2, itemService.searchItemBySubString("SiLvEr").size());
    }

    @Test
    void testGetAllItemsOfUser() {
        log.debug("search items test");
        userRepository.addUser(user1);
        userRepository.addUser(user2);
        itemRepository.addItem(1L, item);
        itemRepository.addItem(2L, item1);
        itemRepository.addItem(1L, item2);
        assertEquals(2, itemService.getAllUserItems(1L).size());
    }
}
