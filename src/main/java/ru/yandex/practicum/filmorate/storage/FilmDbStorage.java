package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

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
        if (!film.getGenres().isEmpty()) {
            String sqlG = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batch = film.getGenres().stream()
                    .map(g -> new Object[]{id, g.getId()})
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sqlG, batch);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (!film.getGenres().isEmpty()) {
            String sqlG = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batch = film.getGenres().stream()
                    .map(g -> new Object[]{film.getId(), g.getId()})
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sqlG, batch);
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT id, name, description, release_date, duration, mpa_id FROM films WHERE id = ?";
        return jdbc.queryForObject(sql, (rs, rn) -> Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), null))
                .build(), id);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT id, name, description, release_date, duration, mpa_id FROM films";
        return jdbc.query(sql, (rs, rn) -> Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), null))
                .build());
    }

    @Override
    public void removeFilm(Long id) {
        jdbc.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbc.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
                "FROM films f LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        return jdbc.query(sql, (rs, rn) -> Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), null))
                .build(), count);
    }

    public Set<Long> getLikes(Long filmId) {
        return new HashSet<>(jdbc.queryForList(
                "SELECT user_id FROM likes WHERE film_id = ?", Long.class, filmId));
    }
}

