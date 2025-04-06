package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        validate(film);
        if (film.getName() == null) {
            String str = "Название фильмо должно быть указано";
            log.error(str);
            throw new ValidationException(str);
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в базу", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            String info = "Id обновляемого фильма не указан";
            log.error("Ошибка при обновлении фильма: {}", info);
            throw new ValidationException(info);
        }
        if (films.containsKey(newFilm.getId())) {
            validate(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм {} обновлён успешно", oldFilm.getName());
            return oldFilm;
        } else {
            String str = "Обновляемый фильм не найден!";
            log.error(str);
            throw new ValidationException(str);
        }
    }

    private void validate(Film film) {
        if (film.getName() == null ||
                film.getName().isBlank() ||
                film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDuration().isNegative()) {
            log.error("Данные о фильме {} не соответствуют необходимым", film.getName());
            throw new ValidationException("Некорректные данные фильма");
        }
    }

    private long getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
