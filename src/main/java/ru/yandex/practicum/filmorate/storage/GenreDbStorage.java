package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;

    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbc.query(sql, this::mapRowToGenre);
    }

    public Genre findById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbc.query(sql, this::mapRowToGenre, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Genre not found: " + id));
    }

    public List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.id";
        return jdbc.query(sql, this::mapRowToGenre, filmId);
    }

    public Map<Long, List<Genre>> getGenresForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) return Collections.emptyMap();
        String placeholders = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = "SELECT fg.film_id, g.id, g.name " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id IN (" + placeholders + ") " +
                "ORDER BY fg.film_id, g.id";
        Map<Long, List<Genre>> map = new HashMap<>();
        jdbc.query(con -> {
            var ps = con.prepareStatement(sql);
            for (int i = 0; i < filmIds.size(); i++) ps.setLong(i+1, filmIds.get(i));
            return ps;
        }, rs -> {
            long fid = rs.getLong("film_id");
            Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
            map.computeIfAbsent(fid, k -> new ArrayList<>()).add(genre);
        });
        return map;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}