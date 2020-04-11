package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {
}
