package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void addValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmController.addFilm(film);
        assertNotNull(addedFilm.getId());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilmWithEarlyReleaseDate() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void updateFilmWithInvalidId() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void getAllFilms() {
        Film film1 = createTestFilm("Film 1");
        Film film2 = createTestFilm("Film 2");

        filmController.addFilm(film1);
        filmController.addFilm(film2);

        assertEquals(2, filmController.getAllFilms().size());
    }

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }
}