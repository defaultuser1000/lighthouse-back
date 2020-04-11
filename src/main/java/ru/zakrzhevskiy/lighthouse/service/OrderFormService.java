package ru.zakrzhevskiy.lighthouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;
import ru.zakrzhevskiy.lighthouse.utils.HTMLPrintUtil;

@Service
public class OrderFormService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    public String generateOrderForm(Order order) {
        User orderOwner = userRepository.findUserById(order.getOrderOwner());
        return HTMLPrintUtil.printPdfReport("OrderForm_ru.html", order, orderOwner);
    }

    public String generateOrderForm(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        User orderOwner = userRepository.findUserById(order.getOrderOwner());
        return HTMLPrintUtil.printPdfReport("OrderForm_ru.html", order, orderOwner);
    }
}
