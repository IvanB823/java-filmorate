package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
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
        if (user.getName() == null || user.getName().isBlank() || user.getName().contains(" ")) {
            log.warn("Предупреждение, поле name пусто, в него будет записан login = {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно создан и добавлен в систему", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            String info = "Id должен быть указан";
            log.error("Ошибка обновления пользователя: {}", info);
            throw new ValidationException(info);
        }
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            String info = "Пользователь не найден";
            log.error("Ошибка обновления пользователя: {}", info);
            throw new ValidationException(info);
        }
        if (newUser.getEmail() != null) {
            log.info("Почта успешно обновлена");
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            log.info("Логин успешно обновлен");
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            log.info("Поле имени пусто, произведена замена на логин");
            oldUser.setName(newUser.getLogin());
        } else {
            log.info("Имя успешно обновлено");
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null) {
            log.info("Дата рождения успешно обновлена");
            oldUser.setBirthday(newUser.getBirthday());
        }
        return oldUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
