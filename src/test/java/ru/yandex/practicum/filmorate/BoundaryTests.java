package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BoundaryTests {
    private FilmController filmController = new FilmController();
    private UserController userController = new UserController();

    @Test
    void filmValidation_BoundaryValues() {
        // Проверка минимально допустимой даты релиза
        Film film1 = new Film();
        film1.setName("Boundary Film");
        film1.setDescription("Desc");
        film1.setReleaseDate(LocalDate.of(1895, 12, 28));
        film1.setDuration(1);
        assertDoesNotThrow(() -> filmController.addFilm(film1));

        // Проверка максимальной длины описания
        Film film2 = new Film();
        film2.setName("Boundary Film");
        film2.setDescription("a".repeat(200));
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(1);
        assertDoesNotThrow(() -> filmController.addFilm(film2));
    }

    @Test
    void userValidation_BoundaryValues() {
        // Проверка сегодняшней даты рождения
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setLogin("testlogin");
        user1.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> userController.createUser(user1));

        // Проверка минимально допустимого логина
        User user2 = new User();
        user2.setEmail("test@example.com");
        user2.setLogin("a"); // Минимальный логин
        user2.setBirthday(LocalDate.of(1990, 1, 1));
        assertDoesNotThrow(() -> userController.createUser(user2));
    }

    @Test
    void nullRequest_ThrowsException() {
        assertThrows(Exception.class, () -> filmController.addFilm(null));
        assertThrows(Exception.class, () -> userController.createUser(null));
    }
}