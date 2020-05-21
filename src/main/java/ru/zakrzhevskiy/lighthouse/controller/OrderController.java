package ru.zakrzhevskiy.lighthouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.Film;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.enums.OrderStatus;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;
import ru.zakrzhevskiy.lighthouse.service.OrderFormService;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderFormService orderFormService;
    private final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    // Yandex.Disk base folder path
    private final String BASE_FOLDER = "";

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Order> orders(Principal principal) {

        String username = principal.getName();
        User user = userRepository.findUserByUsername(username).get();

        return orderRepository.findByOrderOwnerOrderByModificationDate(user.getId());
    }

    @RequestMapping(
            path = "/order/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, Principal principal) throws URISyntaxException {
        log.info("Request to create order: {}", order);

        Order lastOrder = orderRepository.findTopByOrderByIdDesc();
        int orderNumber;
        if (lastOrder != null) {
            orderNumber = lastOrder.getOrderNumber() + 1;
        } else {
            orderNumber = 1001;
        }

        User creatorAndOwner = userRepository.findUserByUsername(principal.getName()).get();

        order.setOrderNumber(orderNumber);
        order.setOrderCreator(creatorAndOwner.getId());
        order.setOrderOwner(creatorAndOwner.getId());
        order.setOrderStatus(OrderStatus.NEW);

        Order result = orderRepository.save(order);

//        result.setOrderForm(orderFormService.generateOrderForm(result));
//        orderRepository.save(result);

        return ResponseEntity.created(new URI("/orders/order/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/order/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order) {
        Order baseOrder = orderRepository.findOrderById(id);
        log.info("Request to update order: {}", baseOrder);
        Order result = orderRepository.save(order);
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/order/{id}/films",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Film> getOrderFilms(@PathVariable Long id) {
        return orderRepository.findOrderById(id).getOrderFilms();
    }

    @RequestMapping(path = "/order/{id}/generateReport", method = RequestMethod.GET)
    public Object generateOrderForm(@PathVariable Long id) {
        return orderFormService.generateOrderForm(id);
    }

    @RequestMapping(path = "/order/{id}/setOrderDiskDestination", method = RequestMethod.PUT)
    public ResponseEntity<?> setOrderDiskDespination(@PathVariable Long id, @RequestParam(name = "path") String destinationPath) {
        Order order = orderRepository.findOrderById(id);

        if (destinationPath.matches(".*")) {
            // TODO: Апи Диска: проверять наличие папки, создавать при необходимости
            order.setOrderDiskDestination(destinationPath);
            return ResponseEntity.ok(orderRepository.save(order).getOrderDiskDestination());
        } else {
            return ResponseEntity.badRequest().body("Not valid disk folder path.");
        }
    }
}
