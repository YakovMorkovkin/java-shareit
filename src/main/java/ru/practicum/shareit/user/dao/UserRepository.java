package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository {
    User addUser(User user);
    User updateUser(Long userId, User user);
    Optional<User> getUserById(Long userId);
    List<User> getAllUsers();
    void deleteUser(Long userId);
}
