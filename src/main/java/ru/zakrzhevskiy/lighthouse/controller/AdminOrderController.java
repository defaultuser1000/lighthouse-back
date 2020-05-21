package ru.zakrzhevskiy.lighthouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Order> orders(@RequestParam(name = "sortBy", defaultValue = "modificationDate") String sortedField) {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, sortedField));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, Principal principal) throws URISyntaxException {
        log.info("Request to create order: {}", order);

        User creatorAndOwner = userRepository.findUserByUsername(principal.getName()).get();

        order.setOrderCreator(creatorAndOwner.getId());

        Order result = orderRepository.save(order);

//        result.setOrderForm(orderFormService.generateOrderForm(result));
//        orderRepository.save(result);

        return ResponseEntity.created(new URI("/orders/order/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/order/{id}"
    )
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        log.info("Request to delete order: {}", orderRepository.findOrderById(id));
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/{id}/userOwner",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getOrderOwner(@PathVariable Long id) {
        Order order = orderRepository.findOrderById(id);
        Optional<User> orderOwner = userRepository.findById(order.getOrderOwner());
        return orderOwner.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/{id}/userCreator",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getOrderCreator(@PathVariable Long id) {
        Order order = orderRepository.findOrderById(id);
        Optional<User> orderCreator = userRepository.findById(order.getOrderCreator());
        return orderCreator.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
