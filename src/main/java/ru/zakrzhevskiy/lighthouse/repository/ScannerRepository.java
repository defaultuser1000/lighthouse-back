package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.Scanner;

public interface ScannerRepository extends JpaRepository<Scanner, Long> {
    Scanner findByName(String name);
}
