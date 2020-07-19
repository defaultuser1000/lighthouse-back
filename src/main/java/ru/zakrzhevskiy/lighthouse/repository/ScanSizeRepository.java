package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.ScanSize;

public interface ScanSizeRepository extends JpaRepository<ScanSize, Long> {
    ScanSize findBySize(String size);
}
