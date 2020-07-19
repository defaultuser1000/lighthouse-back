package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.FilmFormat;

public interface FilmFormatRepository extends JpaRepository<FilmFormat, Long> {
}
