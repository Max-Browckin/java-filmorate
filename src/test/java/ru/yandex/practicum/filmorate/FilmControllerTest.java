package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void addFilm_ValidFilm_ReturnsFilmWithId() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film result = filmController.addFilm(film);

        assertNotNull(result.getId());
        assertEquals(film.getName(), result.getName());
    }

    @Test
    void addFilm_EmptyName_ThrowsValidationException() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilm_TooLongDescription_ThrowsValidationException() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilm_TooEarlyReleaseDate_ThrowsValidationException() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilm_NegativeDuration_ThrowsValidationException() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void updateFilm_NonExistingId_ThrowsValidationException() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void getAllFilms_EmptyList_ReturnsEmptyList() {
        List<Film> films = filmController.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void getAllFilms_WithFilms_ReturnsAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(90);
        filmController.addFilm(film2);

        List<Film> films = filmController.getAllFilms();
        assertEquals(2, films.size());
    }
}