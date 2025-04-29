package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbc;

    public List<Mpa> findAll() {
        return jdbc.query("SELECT * FROM mpa ORDER BY id", this::mapRowToMpa);
    }

    public Mpa findById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbc.query(sql, this::mapRowToMpa, id).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA rating not found"));
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
