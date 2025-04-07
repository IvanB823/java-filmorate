package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTests {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void getAllUsersAndPostUserTest() {
        User user = new User("Sean84@gmail.com", "OrK6qvD8JY", "Marcia Cartwright", LocalDate.of(1992, 12, 8));
        User createdUser = userController.addUser(user);
        List<User> users = new ArrayList<>(userController.findAllUsers());
        assertNotNull(createdUser);
        assertEquals(1, users.size());
        assertEquals("Sean84@gmail.com", users.getFirst().getEmail());
    }

    @Test
    void updateUserTest() {
        User user = new User("maddy@mail.ru", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        User newUser = new User(1L,"igor@mail.ru", "Igor_with_granny's_car", "Юрий Борисов", LocalDate.of(1992, 12, 8));
        userController.addUser(user);
        userController.updateUser(newUser);
        List<User> users = new ArrayList<>(userController.findAllUsers());
        assertEquals("igor@mail.ru", users.getFirst().getEmail());
        assertEquals(1, users.getFirst().getId());
    }

    @Test
    void changingNameForLoginIfBlankOrNullTest() {
        User user = new User("maddy@mail.ru", "Mikey_danceLover", "", LocalDate.of(1999, 3, 25));
        User user2 = new User(1L,"igor@mail.ru", "Igor_with_granny's_car", null, LocalDate.of(1992, 12, 8));
        userController.addUser(user);
        userController.addUser(user2);
        List<User> users = new ArrayList<>(userController.findAllUsers());
        assertEquals("Mikey_danceLover", users.getFirst().getName());
        assertEquals("Igor_with_granny's_car", users.get(1).getName());
    }

    @Test
    void emptyAndNullLoginTest() {
        User user = new User("maddy@mail.ru", "", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        User user2 = new User("maddy@mail.ru", null, "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user2);
        });
    }

    @Test
    void emptyAndNullEmailTest() {
        User user = new User("", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        User user2 = new User(null, "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user2);
        });
    }

    @Test
    void birthdayInFutureTest() {
        User user = new User("maddy@mail.ru", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(2999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
    }

    @Test
    void updatingNonExistentUserTest() {
        User user = new User(1L, "maddy@mail.ru", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(2999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
    }
}
