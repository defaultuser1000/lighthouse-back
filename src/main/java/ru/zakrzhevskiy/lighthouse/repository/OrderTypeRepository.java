package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.price.OrderType;

public interface OrderTypeRepository extends JpaRepository<OrderType, Long> {
    OrderType findByName(String name);
}
