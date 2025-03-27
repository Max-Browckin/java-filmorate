package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createValidUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.createUser(user);
        assertNotNull(createdUser.getId());
        assertEquals("testlogin", createdUser.getName());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void createUserWithEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void updateUserWithInvalidId() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void getAllUsers() {
        User user1 = createTestUser("user1@mail.com", "user1");
        User user2 = createTestUser("user2@mail.com", "user2");

        userController.createUser(user1);
        userController.createUser(user2);

        assertEquals(2, userController.getAllUsers().size());
    }

    private User createTestUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}