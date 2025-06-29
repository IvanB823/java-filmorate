package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void testFindAll() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.addUser(user);
        List<User> users = new ArrayList<>(userController.findAllUsers());

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("ivan@example.com", users.getFirst().getEmail());
    }

    @Test
    void testAddUserValidUser() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.addUser(user);

        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("Иван Иванов", createdUser.getName());
    }

    @Test
    void testUpdateValidUser() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.addUser(user);

        User user2 = User.builder()
                .id(1L)
                .email("ivan@example.com")
                .login("user321")
                .name("Пётр Петров")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.updateUser(user2);

        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("Пётр Петров", createdUser.getName());
        assertEquals("user321", createdUser.getLogin());
    }

    @Test
    void testEmptyLogin() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void testNullLogin() {
        User user = User.builder()
                .email("ivan@example.com")
                .login(null)
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void testLoginWithSpaces() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void testEmptyEmail() {
        User user = User.builder()
                .email("")
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void testNullEmail() {
        User user = User.builder()
                .email(null)
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void testEmailWithoutAtSign() {
        User user = User.builder()
                .email("invalid-email")
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void testEmptyName() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.addUser(user);

        assertNotNull(createdUser);
        assertEquals("user123", createdUser.getName());
    }

    @Test
    void testNullName() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name(null)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.addUser(user);

        assertNotNull(createdUser);
        assertEquals("user123", createdUser.getName());
    }

    @Test
    void testBirthdayInFuture() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertTrue(exception.getMessage().contains("Дата рождения не может быть в будущем"));
    }

    @Test
    void testBirthdayToday() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.now())
                .build();

        User createdUser = userController.addUser(user);

        assertNotNull(createdUser);
        assertEquals(LocalDate.now(), createdUser.getBirthday());
    }

    @Test
    void testUpdateNonExistingUser() {
        User user = User.builder()
                .id(999L)
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userController.updateUser(user);
        });

        assertTrue(exception.getMessage().contains("Пользователь с id = " + user.getId() + " не найден"));
    }
}
