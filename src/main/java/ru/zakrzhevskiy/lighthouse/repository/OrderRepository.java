package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOrderById(Long id);
    List<Order> getOrdersByOrderCreator(User creator);

    List<Order> findByOrderOwnerOrderByModificationDateDesc(User user);

    Page<Order> findByOrderOwner(User user, Pageable pageable);

    List<Order> findByOrderOwner(User user);
    Order findTopByOrderByIdDesc();
}
