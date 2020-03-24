package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOrderById(Long id);
}
