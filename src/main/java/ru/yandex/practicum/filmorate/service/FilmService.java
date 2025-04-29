package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        Mpa mpa = mpaStorage.findById(film.getMpa().getId());
        film.setMpa(mpa);
        Set<Genre> genres = Collections.emptySet();
        if (film.getGenres() != null) {
            genres = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .map(g -> genreStorage.findById(g.getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        film.setGenres(genres);
        Film created = filmStorage.addFilm(film);
        return getFilmById(created.getId());
    }

    public Film updateFilm(Film film) {
        filmStorage.getFilmById(film.getId());
        validateReleaseDate(film);
        Mpa mpa = mpaStorage.findById(film.getMpa().getId());
        film.setMpa(mpa);
        Set<Genre> genres = Collections.emptySet();
        if (film.getGenres() != null) {
            genres = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .map(g -> genreStorage.findById(g.getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        film.setGenres(genres);
        filmStorage.updateFilm(film);
        return getFilmById(film.getId());
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        film.setGenres(new LinkedHashSet<>(genreStorage.getGenresForFilm(id)));
        film.setLikes(filmStorage.getLikes(id));
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms().stream()
                .map(f -> getFilmById(f.getId()))
                .collect(Collectors.toList());
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
        return filmStorage.getPopularFilms(count).stream()
                .map(f -> getFilmById(f.getId()))
                .collect(Collectors.toList());
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date must not be earlier than 1895-12-28");
        }
    }
}
