package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testAddAndFindUser() {
        User user = new User(null, "test@mail.com", "testlogin", "Test Name", LocalDate.of(1990, 1, 1));
        User created = userStorage.addUser(user);

        Optional<User> fromDb = Optional.of(userStorage.getUserById(created.getId()));

        assertThat(fromDb)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", created.getId())
                                .hasFieldOrPropertyWithValue("email", "test@mail.com")
                                .hasFieldOrPropertyWithValue("login", "testlogin")
                );
    }
}
