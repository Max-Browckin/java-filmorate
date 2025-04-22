package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    public FilmController() {
        this.filmService = new FilmService(new InMemoryFilmStorage());
        this.userService = new UserService(new InMemoryUserStorage());
    }

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Fetching all films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("Fetching film with id {}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Adding film: {}", film);
        validateFilm(film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Updating film: {}", film);
        validateFilm(film);
        try {
            return filmService.updateFilm(film);
        } catch (NotFoundException e) {
            log.warn("Film not found: {}", film.getId());
            throw new ValidationException("Film not found");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        log.info("Deleting film with id {}", id);
        filmService.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("User {} likes film {}", userId, id);
        userService.getUserById(userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("User {} removes like from film {}", userId, id);
        userService.getUserById(userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Fetching top {} popular films", count);
        return filmService.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film cannot be null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Film name is invalid");
            throw new ValidationException("Film name is invalid");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Film description is too long");
            throw new ValidationException("Description length exceeds 200 characters");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Film release date is invalid");
            throw new ValidationException("Release date is invalid");
        }
        if (film.getDuration() <= 0) {
            log.warn("Film duration is invalid");
            throw new ValidationException("Duration must be positive");
        }
    }
}


