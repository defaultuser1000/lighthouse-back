package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.PriceList;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {

    PriceList findPriceListById(Long id);

}
