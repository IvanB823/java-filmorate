package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTests {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void getAllUsersAndPostUserTest() {
        User user = new User("igor@mail.ru", "Igor_with_granny's_car", "Юрий Борисов", LocalDate.of(1992, 12, 8));
        User createdUser = userController.postUser(user);
        List<User> users = new ArrayList<>(userController.getAllUsers());
        assertNotNull(createdUser);
        assertEquals(1, users.size());
        assertEquals("igor@mail.ru", users.getFirst().getEmail());
    }

    @Test
    void updateUserTest() {
        User user = new User("maddy@mail.ru", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        User newUser = new User(1,"igor@mail.ru", "Igor_with_granny's_car", "Юрий Борисов", LocalDate.of(1992, 12, 8));
        userController.postUser(user);
        userController.updateUser(newUser);
        List<User> users = new ArrayList<>(userController.getAllUsers());
        assertEquals("igor@mail.ru", users.getFirst().getEmail());
        assertEquals(1, users.getFirst().getId());
    }

    @Test
    void changingNameForLoginIfBlankOrNullTest() {
        User user = new User("maddy@mail.ru", "Mikey_danceLover", "", LocalDate.of(1999, 3, 25));
        User user2 = new User(1,"igor@mail.ru", "Igor_with_granny's_car", null, LocalDate.of(1992, 12, 8));
        userController.postUser(user);
        userController.postUser(user2);
        List<User> users = new ArrayList<>(userController.getAllUsers());
        assertEquals("Mikey_danceLover", users.getFirst().getName());
        assertEquals("Igor_with_granny's_car", users.get(1).getName());
    }

    @Test
    void emptyAndNullLoginTest() {
        User user = new User("maddy@mail.ru", "", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        User user2 = new User( "maddy@mail.ru", null, "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.postUser(user);
        });
        assertThrows(ValidationException.class, () -> {
            userController.postUser(user2);
        });
    }

    @Test
    void emptyAndNullEmailTest() {
        User user = new User("", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        User user2 = new User( null, "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(1999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.postUser(user);
        });
        assertThrows(ValidationException.class, () -> {
            userController.postUser(user2);
        });
    }

    @Test
    void birthdayInFutureTest() {
        User user = new User("maddy@mail.ru", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(2999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.postUser(user);
        });
    }

    @Test
    void updatingNonExistentUserTest() {
        User user = new User(1, "maddy@mail.ru", "Mikey_danceLover", "Мэдисон Майки", LocalDate.of(2999, 3, 25));
        assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });
    }
}
