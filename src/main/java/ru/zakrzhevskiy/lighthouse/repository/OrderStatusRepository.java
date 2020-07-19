package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    OrderStatus findOrderStatusByDisplayName(String displayName);

}
