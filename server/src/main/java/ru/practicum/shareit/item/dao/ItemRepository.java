package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Transactional
    @Modifying
    @Query("update Item i set i.owner = ?1, i.name = ?2, i.description = ?3, i.available = ?4 where i.id = ?5")
    void update(User owner, String name, String description, Boolean available, Long itemId);

    List<Item> findByOwnerId(Long userId);

    Page<Item> findByOwnerId(Long userId, Pageable pageable);

    Page<Item> findByAvailableIsTrueAndDescriptionContainsIgnoreCaseOrNameContainsIgnoreCase(String textD, String textN,
                                                                                             Pageable pageable);

    List<Item> findAllByItemRequestId(Long requestId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Item ")
    void deleteAll();
}
