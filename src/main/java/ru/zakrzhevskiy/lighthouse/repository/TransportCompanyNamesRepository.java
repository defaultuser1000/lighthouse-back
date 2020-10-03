package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.TransportCompanyName;

import java.util.List;

public interface TransportCompanyNamesRepository extends JpaRepository<TransportCompanyName, Long> {

    List<TransportCompanyName> findByName(String name);

}
