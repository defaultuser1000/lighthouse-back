package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.Price;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findPriceById(Long id);

}