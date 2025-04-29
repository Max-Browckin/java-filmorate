package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        film.setId(id);
        updateFilmGenres(film);
        return getFilmById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        deleteFilmGenres(film.getId());
        updateFilmGenres(film);
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        Film film = jdbc.query(sql, this::makeFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        List<Genre> genres = genreStorage.getGenresForFilm(film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
        film.setLikes(getLikes(film.getId()));
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbc.query(sql, this::makeFilm);
        for (Film f : films) {
            f.setMpa(mpaStorage.findById(f.getMpa().getId()));
            List<Genre> genres = genreStorage.getGenresForFilm(f.getId());
            f.setGenres(new LinkedHashSet<>(genres));
            f.setLikes(getLikes(f.getId()));
        }
        return films;
    }

    @Override
    public void removeFilm(Long id) {
        getFilmById(id);
        jdbc.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) AS cnt FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id ORDER BY cnt DESC LIMIT ?";
        List<Film> films = jdbc.query(sql, this::makeFilm, count);
        for (Film f : films) {
            f.setMpa(mpaStorage.findById(f.getMpa().getId()));
            List<Genre> genres = genreStorage.getGenresForFilm(f.getId());
            f.setGenres(new LinkedHashSet<>(genres));
            f.setLikes(getLikes(f.getId()));
        }
        return films;
    }

    private Set<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbc.queryForList(sql, Long.class, filmId));
    }

    private void deleteFilmGenres(Long filmId) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
    }

    private void updateFilmGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbc.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), null))
                .genres(new LinkedHashSet<>())
                .likes(new LinkedHashSet<>())
                .build();
    }
}

