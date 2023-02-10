package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.UserFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository("UserRepositoryImpl")
@RequiredArgsConstructor
@Primary
public class UserRepositoryImpl implements UserRepository {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        Long key;
        if (!emailIsDuplicated(user.getEmail())) {
            key = generatorId();
            user.setId(key);
            users.put(key, user);
        } else {
            throw new EmailDuplicateException(user.getEmail());
        }
        return users.get(key);
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (emailIsDuplicated(user.getEmail())) {
            throw new EmailDuplicateException(user.getEmail());
        } else if (users.containsKey(userId)) {
            User originalUser = users.get(userId);
            user.setId(userId);
            user.setName(user.getName() != null ? user.getName() : originalUser.getName());
            user.setEmail(user.getEmail() != null ? user.getEmail() : originalUser.getEmail());
            users.put(userId, user);
        } else {
            throw new UserFoundException(userId);
        }
        return users.get(userId);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private Long generatorId() {
        return ++id;
    }

    private boolean emailIsDuplicated(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }
}
