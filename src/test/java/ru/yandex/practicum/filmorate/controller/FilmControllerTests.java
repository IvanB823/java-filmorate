package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTests {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage(),
                new RatingService(new RatingDbStorage(new JdbcTemplate())),
                new GenreService(new GenreDbStorage(new JdbcTemplate()))));
    }

    @Test
    void getAllFilmsAndPostFilmTest() {
            Film film = new Film();
                film.setName("Anora");
                film.setDescription("Censored");
                film.setReleaseDate(LocalDate.of(2024, 10, 18));
                film.setDuration(Duration.ofMinutes(139));
        filmController.addFilm(film);
        assertNotNull(filmController.getAllFilms());
    }

    @Test
    void updateExistingFilmTest() {
        Film film = new Film();
                film.setName("Anora");
                film.setDescription("Censored");
                film.setReleaseDate(LocalDate.of(2024, 10, 18));
                film.setDuration(Duration.ofMinutes(139));

        Film film2 = new Film();
                film2.setId(1L);
                film2.setName("Anora2");
                film2.setDescription("Censored");
                film2.setReleaseDate(LocalDate.of(2024, 10, 18));
                film2.setDuration(Duration.ofMinutes(139));
        filmController.addFilm(film);
        filmController.updateFilm(film2);
        List<Film> films = new ArrayList<>(filmController.getAllFilms());
        assertEquals("Anora2", films.getFirst().getName());
    }

    @Test
    void updatingNonExistentFilmTest() {
        Film film2 = new Film();
                film2.setId(1L);
                film2.setName("Anora2");
                film2.setDescription("Censored");
                film2.setReleaseDate(LocalDate.of(2024, 10, 18));
                film2.setDuration(Duration.ofMinutes(139));
        assertThrows(NotFoundException.class, () -> {
            filmController.updateFilm(film2);
        });
    }

    @Test
    void lengthOfDescriptionTest() {
        Film film = new Film();
                film.setName("Anora2");
                film.setDescription("1".repeat(201));
                film.setReleaseDate(LocalDate.of(2024, 10, 18));
                film.setDuration(Duration.ofMinutes(139));
        Film film2 = new Film();
                film2.setName("Anora2");
                film2.setDescription("1".repeat(200));
                film2.setReleaseDate(LocalDate.of(2024, 10, 18));
                film2.setDuration(Duration.ofMinutes(139));
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Film film3 = filmController.addFilm(film2);
        assertEquals(200, film3.getDescription().length());
    }

    @Test
    void emptyAndNullNameTest() {
        Film film = new Film();
                film.setName("");
                film.setDescription("Censored");
                film.setReleaseDate(LocalDate.of(2024, 10, 18));
                film.setDuration(Duration.ofMinutes(139));
        Film film2 = new Film();
                film.setName(null);
                film.setDescription("Censored");
                film.setReleaseDate(LocalDate.of(2024, 10, 18));
                film.setDuration(Duration.ofMinutes(139));
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film2);
        });
    }

    @Test
    void releaseDateTest() {
        Film film1 = new Film();
                film1.setName("Anora");
                film1.setDescription("Censored");
                film1.setReleaseDate(LocalDate.of(1895, 12, 28));
                film1.setDuration(Duration.ofMinutes(139));
        Film film2 = new Film();
                film2.setName("Anora2");
                film2.setDescription("Censored");
                film2.setReleaseDate(LocalDate.of(1894, 10, 18));
                film2.setDuration(Duration.ofMinutes(139));
        assertThrows(ConditionsNotMetException.class, () -> {
            filmController.updateFilm(film1);
        });
        assertThrows(ConditionsNotMetException.class, () -> {
            filmController.updateFilm(film2);
        });
    }

    @Test
    void negativeAndZeroDurationTests() {
        Film film1 = new Film();
                film1.setName("Anora");
                film1.setDescription("Censored");
                film1.setReleaseDate(LocalDate.of(1895, 12, 28));
                film1.setDuration(Duration.ofMinutes(-1));
        Film film2 = new Film();
                film2.setName("Anora2");
                film2.setDescription("Censored");
                film2.setReleaseDate(LocalDate.of(1894, 10, 18));
                film2.setDuration(Duration.ofMinutes(0));
        assertThrows(ConditionsNotMetException.class, () -> {
            filmController.updateFilm(film1);
        });
        assertThrows(ConditionsNotMetException.class, () -> {
            filmController.updateFilm(film2);
        });
    }
}

