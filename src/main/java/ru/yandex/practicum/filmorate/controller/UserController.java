package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService(new InMemoryUserStorage());
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@Positive @PathVariable Long id) {
        log.info("Fetching user with id {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);
        validateUser(user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Updating user: {}", user);
        validateUser(user);
        try {
            return userService.updateUser(user);
        } catch (NotFoundException e) {
            log.warn("User not found: {}", user.getId());
            throw new ValidationException("User not found");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Positive @PathVariable Long id) {
        log.info("Deleting user with id {}", id);
        userService.removeUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@Positive @PathVariable Long id,
                          @Positive @PathVariable Long friendId) {
        log.info("User {} adds friend {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@Positive @PathVariable Long id,
                             @Positive @PathVariable Long friendId) {
        log.info("User {} removes friend {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@Positive @PathVariable Long id) {
        log.info("Fetching friends of user {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@Positive @PathVariable Long id,
                                       @Positive @PathVariable Long otherId) {
        log.info("Fetching common friends of users {} and {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Email is invalid");
            throw new ValidationException("Email is invalid");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Login is invalid");
            throw new ValidationException("Login is invalid");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Name set to login for user {}", user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Birthday is invalid");
            throw new ValidationException("Birthday is invalid");
        }
    }
}
