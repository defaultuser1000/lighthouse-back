package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.price.OrderType;
import ru.zakrzhevskiy.lighthouse.repository.OrderTypeRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/settings/order-type")
public class AdminOrderTypeController {

    private final Logger log = LoggerFactory.getLogger(AdminOrderTypeController.class);
    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<OrderType> orderTypes() {
        return orderTypeRepository.findAll();
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getOrderType(@PathVariable Long id) {
        Optional<OrderType> orderType = orderTypeRepository.findById(id);
        return orderType.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderType> createOrderType(@Valid @RequestBody OrderType orderType) throws URISyntaxException {
        log.info("Request to create order type: {}", orderType);

        OrderType result = orderTypeRepository.save(orderType);

        return ResponseEntity.created(new URI("/admin/settings/order-type/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateOrderType(@PathVariable Long id, @Valid @RequestBody OrderType orderType) {
        OrderType baseOrderType = orderTypeRepository.findById(id).get();
        log.info("Request to update order type: {}", baseOrderType);
        OrderType result = orderTypeRepository.save(orderType);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteOrderType(@PathVariable Long id) {
        log.info("Request to delete order type: {}", orderTypeRepository.findById(id).get());
        orderTypeRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

}
