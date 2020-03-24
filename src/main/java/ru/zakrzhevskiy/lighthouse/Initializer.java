package ru.zakrzhevskiy.lighthouse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

@Component
public class Initializer implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Initializer(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... strings) {

    }
}
