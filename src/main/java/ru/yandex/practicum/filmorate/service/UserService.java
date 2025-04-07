package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String message = "Электронная почта не должна быть пустой и должна содержать символ @";
            log.error("Failed to create user: {}", message);
            throw new ValidationException(message);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String message = "Логин не должен быть пустым и содержать пробелы";
            log.error("Failed to create user: {}", message);
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не должна быть в будущем";
            log.error("Failed to create user: {}", message);
            throw new ValidationException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, в качестве имени будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        return userStorage.saveUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            String message = "Id пользователя должен быть указан";
            throw new ConditionsNotMetException(message);
        }
        if (userStorage.userExists(newUser.getId())) {
            if (!newUser.getEmail().contains("@")) {
                throw new ValidationException("Электронная пользователя почта должна содержать символ @");
            }
            if (newUser.getLogin().contains(" ")) {
                throw new ValidationException("Логин пользователя не может содержать пробелы");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения пользователя не должна быть в будущем");
            }
            return userStorage.putUser(newUser);
        }
        log.error("Пользователь с id {} не найден", newUser.getId());
        throw new NotFoundException("Не найден пользователь с id " + newUser.getId());
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден друг с id: " + friendId));

        userStorage.addFriend(user, friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден друг с id: " + friendId));

        userStorage.removeFriend(user, friend);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        return userStorage.findFriends(user);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        User otherUser = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Не найден второй пользователь с id: " + otherId));
        return userStorage.findCommonFriends(user, otherUser);
    }

    public User getUserById(Long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + id));
    }
}
