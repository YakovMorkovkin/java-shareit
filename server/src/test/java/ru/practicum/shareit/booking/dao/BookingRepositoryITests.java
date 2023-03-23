package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookingRepositoryITests {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    List<Item> items;

    User booker = User.builder()
            .id(1L)
            .name("Booker")
            .email("booker@test.ru")
            .build();

    User owner = User.builder()
            .id(2L)
            .name("Item owner")
            .email("item_owner@test.ru")
            .build();
    Item item = Item.builder()
            .id(1L)
            .owner(owner)
            .description("test item")
            .available(true)
            .name("item")
            .build();

    @BeforeAll
    public void addBookings() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRepository.save(Item.builder()
                .owner(owner)
                .description("test item")
                .available(true)
                .name("item")
                .build());
        bookingRepository.save(Booking.builder()
                .status(Status.WAITING)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build());
        bookingRepository.save(Booking.builder()
                .status(Status.WAITING)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build());
        bookingRepository.save(Booking.builder()
                .status(Status.WAITING)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());
        bookingRepository.save(Booking.builder()
                .status(Status.WAITING)
                .booker(owner)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());
        items = new ArrayList<>();
        items.add(item);
    }

    @Test
    void update() {
        bookingRepository.update(Status.REJECTED, 4L);
        int actualSize = bookingRepository.findAllByBookerIdAndStatus(2L, Status.REJECTED,
                PageRequest.of(0, 10)).getContent().size();
        assertEquals(1, actualSize);
    }

    @Test
    void findAllByItemId() {
        List<Booking> actualBookings = bookingRepository.findAllByItemId(1L);
        assertEquals(4, actualBookings.size());
    }

    @Test
    void findAllByBookerId() {
        Page<Booking> actualBookings = bookingRepository.findAllByBookerId(1L, PageRequest.of(0, 10));
        assertEquals(3, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStatus(1L, Status.WAITING,
                PageRequest.of(0, 10));
        assertEquals(3, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(1L, now,
                now, PageRequest.of(0, 10));
        assertEquals(1, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsBefore() {
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBefore(1L, now,
                now, PageRequest.of(0, 10));
        assertEquals(1, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterAndEndIsAfter() {
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfter(1L, now,
                now, PageRequest.of(0, 10));
        assertEquals(1, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdNotAndItemIn() {
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdNotAndItemIn(2L, items,
                PageRequest.of(0, 10));
        assertEquals(3, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdNotAndItemInAndStatus() {
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdNotAndItemInAndStatus(2L, items,
                Status.WAITING, PageRequest.of(0, 10));
        assertEquals(3, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsAfter() {
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsAfter(2L,
                items, now, now, PageRequest.of(0, 10));
        assertEquals(1, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsBefore() {
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsBefore(2L,
                items, now, now, PageRequest.of(0, 10));
        assertEquals(1, actualBookings.getContent().size());
    }

    @Test
    void findAllByBookerIdNotAndItemInAndStartIsAfterAndEndIsAfter() {
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> actualBookings = bookingRepository.findAllByBookerIdNotAndItemInAndStartIsAfterAndEndIsAfter(2L,
                items, now, now, PageRequest.of(0, 10));
        assertEquals(1, actualBookings.getContent().size());
    }

    @Test
    void findAllByItemIdAndBookerId() {
        List<Booking> actualBookings = bookingRepository.findAllByItemIdAndBookerId(1L, 1L);
        assertEquals(3, actualBookings.size());
    }

    @AfterAll
    public void deleteBookings() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}