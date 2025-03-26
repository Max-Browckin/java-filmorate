package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser_ValidUser_ReturnsUserWithId() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userController.createUser(user);

        assertNotNull(result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void createUser_EmptyEmail_ThrowsValidationException() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUser_InvalidEmailFormat_ThrowsValidationException() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUser_EmptyLogin_ThrowsValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUser_LoginWithSpaces_ThrowsValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUser_EmptyName_SetsNameFromLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userController.createUser(user);
        assertEquals(user.getLogin(), result.getName());
    }

    @Test
    void createUser_FutureBirthday_ThrowsValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void updateUser_NonExistingId_ThrowsValidationException() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void getAllUsers_EmptyList_ReturnsEmptyList() {
        List<User> users = userController.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void getAllUsers_WithUsers_ReturnsAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userController.createUser(user2);

        List<User> users = userController.getAllUsers();
        assertEquals(2, users.size());
    }
}