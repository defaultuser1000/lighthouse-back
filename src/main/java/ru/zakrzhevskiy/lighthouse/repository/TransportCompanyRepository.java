package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.TransportCompany;

public interface TransportCompanyRepository extends JpaRepository<TransportCompany, Long> {
}
