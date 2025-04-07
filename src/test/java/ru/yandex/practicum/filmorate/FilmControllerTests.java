package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
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
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    void getAllFilmsAndPostFilmTest() {
        Film film = Film.builder()
                .name("Anora")
                .description("Censored")
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        filmController.addFilm(film);
        assertNotNull(filmController.getAllFilms());
    }

    @Test
    void updateExistingFilmTest() {
        Film film = Film.builder()
                .name("Anora")
                .description("Censored")
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        Film film2 = Film.builder()
                .id(1L)
                .name("Anora2")
                .description("Censored")
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        filmController.addFilm(film);
        filmController.updateFilm(film2);
        List<Film> films = new ArrayList<>(filmController.getAllFilms());
        assertEquals("Anora2", films.getFirst().getName());
    }

    @Test
    void updatingNonExistentFilmTest() {
        Film film2 = Film.builder()
                .id(1L)
                .name("Anora2")
                .description("Censored")
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film2);
        });
    }

    @Test
    void lengthOfDescriptionTest() {
        Film film = Film.builder()
                .name("Anora2")
                .description("1".repeat(201))
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        Film film2 = Film.builder()
                .name("Anora2")
                .description("1".repeat(200))
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        Film film3 = filmController.addFilm(film2);
        assertEquals(200, film3.getDescription().length());
    }

    @Test
    void emptyAndNullNameTest() {
        Film film = Film.builder()
                .name("")
                .description("Censored")
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        Film film2 = Film.builder()
                .name(null)
                .description("Censored")
                .releaseDate(LocalDate.of(2024, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();

        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film2);
        });
    }

    @Test
    void releaseDateTest() {
        Film film1 = Film.builder()
                .name("Anora")
                .description("Censored")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(Duration.ofMinutes(139))
                .build();
        Film film2 = Film.builder()
                .name("Anora2")
                .description("Censored")
                .releaseDate(LocalDate.of(1894, 10, 18))
                .duration(Duration.ofMinutes(139))
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film1);
        });
        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film2);
        });
    }

    @Test
    void negativeAndZeroDurationTests() {
        Film film1 = Film.builder()
                .name("Anora")
                .description("Censored")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(Duration.ofMinutes(-1))
                .build();
        Film film2 = Film.builder()
                .name("Anora2")
                .description("Censored")
                .releaseDate(LocalDate.of(1894, 10, 18))
                .duration(Duration.ofMinutes(0))
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film1);
        });
        assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film2);
        });
    }
}

