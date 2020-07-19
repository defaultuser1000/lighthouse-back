package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
