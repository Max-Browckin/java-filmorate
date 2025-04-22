package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Film getFilmById(Long id);
    List<Film> getAllFilms();
    void removeFilm(Long id);
    void addLike(Long filmId, Long userId);
    void removeLike(Long filmId, Long userId);
    List<Film> getPopularFilms(int count);
}
