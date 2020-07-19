package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.Price;
import ru.zakrzhevskiy.lighthouse.model.price.PriceList;

import java.util.Optional;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {

    PriceList findPriceListById(Long id);

}
