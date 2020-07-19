package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.Process;

public interface ProcessRepository extends JpaRepository<Process, Long> {
}
