package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query("update User u set u.name = ?1, u.email = ?2 where u.id = ?3")
    void update(String name, String email, Long userId);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM User ")
    void deleteAll();
}
