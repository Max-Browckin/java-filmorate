package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public Film addFilm(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void removeFilm(Long id) {
        filmStorage.removeFilm(id);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date must not be earlier than 1895-12-28");
        }
        if (film.getMpa() == null || film.getMpa().getId() == 0) {
            throw new NotFoundException("MPA rating is required");
        }

        Mpa mpa = mpaStorage.findById(film.getMpa().getId());
        film.setMpa(mpa);

        if (film.getGenres() != null) {
            Set<Genre> genres = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .map(g -> genreStorage.findById(g.getId()))
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }
    }
}
