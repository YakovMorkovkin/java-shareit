package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Transactional
    @Modifying
    @Query("update Booking b set b.status = ?1  where b.id = ?2")
    void update(Status status, Long bookingId);

    List<Booking> findAllByItemId(Long itemId);

    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime t1, LocalDateTime t2,
                                                                 Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsBefore(Long bookerId, LocalDateTime t1, LocalDateTime t2,
                                                                  Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsAfterAndEndIsAfter(Long bookerId, LocalDateTime t1, LocalDateTime t2,
                                                                Pageable pageable);

    Page<Booking> findAllByBookerIdNotAndItemIn(Long userId, List<Item> ownerItems, Pageable pageable);

    Page<Booking> findAllByBookerIdNotAndItemInAndStatus(Long userId, List<Item> ownerItems, Status status,
                                                         Pageable pageable);

    Page<Booking> findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsAfter(Long userId, List<Item> ownerItems,
                                                                             LocalDateTime t1, LocalDateTime t2,
                                                                             Pageable pageable);

    Page<Booking> findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsBefore(Long userId, List<Item> ownerItems,
                                                                              LocalDateTime t1, LocalDateTime t2,
                                                                              Pageable pageable);

    Page<Booking> findAllByBookerIdNotAndItemInAndStartIsAfterAndEndIsAfter(Long userId, List<Item> ownerItems,
                                                                            LocalDateTime t1, LocalDateTime t2,
                                                                            Pageable pageable);

    List<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Booking")
    void deleteAll();
}
