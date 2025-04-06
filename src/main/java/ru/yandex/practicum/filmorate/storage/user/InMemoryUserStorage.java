package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage  implements UserStorage{

    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User saveUser(User user) {
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @Override
    public User putUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String info = "Email должен быть корректным и не пустым";
            log.error("Ошибка при добавлении пользователя: {}", info);
            throw new ValidationException(info);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String info = "Логин не должен быть пустым и не должен содержать пробелы";
            log.error("Ошибка при добавлении пользователя: {}", info);
            throw new ValidationException(info);
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            String info = "Дата рождения не может быть в будущем";
            log.error("Ошибка при добавлении пользователя: {}", info);
            throw new ValidationException(info);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Предупреждение, поле name пусто, в него будет записан login = {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно создан и добавлен в систему", user.getName());
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        log.info("Пользователь {}, добавился в друзья пользователю {}", friend, user);
    }

    @Override
    public void removeFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        log.info("Пользователь {}, удалён из друзей пользователя {}", user, friend);
    }

    @Override
    public List<User> findFriends(User user) {
        return user.getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(User user, User otherUser) {
        log.info("Получен список общих друзей пользователей {}, {}", user, otherUser);
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::findUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }


    public boolean userExists(Long userId) {
        return users.containsKey(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
