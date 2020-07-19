package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.OrderStatus;
import ru.zakrzhevskiy.lighthouse.repository.OrderStatusRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


@RestController
@RequestMapping("/admin/settings/order-status")
public class AdminOrderStatusController {
    
    private final Logger log = LoggerFactory.getLogger(AdminOrderStatusController.class);
    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<OrderStatus> orderStatuses(
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "modificationDate") String sortBy
    ) {
        return orderStatusRepository.findAll(Sort.by(direction, sortBy));
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getOrderStatus(@PathVariable Long id) {
        Optional<OrderStatus> orderStatus = orderStatusRepository.findById(id);
        return orderStatus.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderStatus> createOrderStatus(@Valid @RequestBody OrderStatus orderStatus) throws URISyntaxException {
        log.info("Request to create order status: {}", orderStatus);

        OrderStatus result = orderStatusRepository.save(orderStatus);

        return ResponseEntity.created(new URI("/admin/settings/order-status/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatus orderStatus) {
        OrderStatus baseOrderStatus = orderStatusRepository.findById(id).get();
        log.info("Request to update order status: {}", baseOrderStatus);
        OrderStatus result = orderStatusRepository.save(orderStatus);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteOrderStatus(@PathVariable Long id) {
        log.info("Request to delete order status: {}", orderStatusRepository.findById(id).get());
        orderStatusRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "/{id}/nextStatus",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getNextOrderStatus(@PathVariable Long id) {
        OrderStatus currentStatus = orderStatusRepository.getOne(id);
        Optional<OrderStatus> nextStatus = orderStatusRepository.findById(currentStatus.getNextStatusId());

        return nextStatus.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
