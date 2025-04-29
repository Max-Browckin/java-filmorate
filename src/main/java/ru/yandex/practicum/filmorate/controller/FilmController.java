package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@Positive @PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@Positive @PathVariable Long id) {
        filmService.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable Long id,
                                                       @PathVariable Long userId) {
        if (id <= 0 || userId <= 0) {
            throw new ValidationException("Film ID and User ID must be positive and non-zero");
        }
        userService.getUserById(userId);
        filmService.addLike(id, userId);
        return ResponseEntity.ok(Map.of("message", "Like added"));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> removeLike(@PathVariable Long id,
                                                          @PathVariable Long userId) {
        if (id <= 0 || userId <= 0) {
            throw new ValidationException("Film ID and User ID must be positive and non-zero");
        }
        userService.getUserById(userId);
        filmService.removeLike(id, userId);
        return ResponseEntity.ok(Map.of("message", "Like removed"));
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            throw new ValidationException("Count must be positive");
        }
        return filmService.getPopularFilms(count);
    }
}

