package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;

    public List<Genre> findAll() {
        return jdbc.query("SELECT * FROM genres ORDER BY id", this::mapRowToGenre);
    }

    public Genre findById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbc.query(sql, this::mapRowToGenre, id).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Genre not found"));
    }

    public List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id";
        return jdbc.query(sql, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
