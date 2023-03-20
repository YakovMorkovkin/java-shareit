package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserRepositoryITests {
    @Autowired
    private UserRepository userRepository;

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@test.ru")
            .build();
    User user1 = User.builder()
            .id(2L)
            .name("user1")
            .email("user1@test.ru")
            .build();

    @BeforeAll
    public void addUsers() {
        userRepository.save(user);
        userRepository.save(user1);
    }

    @Test
    void update() {
        String name = "Update";
        String email = "update@test.ru";
        userRepository.update(name, email, 1L);
        User updatedUser = userRepository.findById(1L).get();
        assertEquals(name, updatedUser.getName());
        assertEquals(email, updatedUser.getEmail());
    }

    @Test
    void findByEmail() {
        assertTrue(userRepository.findByEmail("user1@test.ru").isPresent());
    }

    @AfterAll
    public void deleteUsers() {
        userRepository.deleteAll();
    }
}