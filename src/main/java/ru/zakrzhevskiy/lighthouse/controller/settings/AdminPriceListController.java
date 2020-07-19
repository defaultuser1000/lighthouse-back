package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.price.Currency;
import ru.zakrzhevskiy.lighthouse.model.price.Price;
import ru.zakrzhevskiy.lighthouse.model.price.PriceList;
import ru.zakrzhevskiy.lighthouse.repository.CurrencyRepository;
import ru.zakrzhevskiy.lighthouse.repository.PriceListRepository;
import ru.zakrzhevskiy.lighthouse.repository.PriceRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/admin/price-list")
public class AdminPriceListController {

    private final Logger log = LoggerFactory.getLogger(AdminPriceListController.class);
    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private UserRepository userRepository;

    // - PRICE_LISTS Part

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<PriceList> priceLists() {
        return priceListRepository.findAll();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PriceList> createPriceList(@Valid @RequestBody PriceList priceList) throws URISyntaxException {
        log.info("Request to create price list: {}", priceList);

        PriceList result = priceListRepository.save(priceList);

        return ResponseEntity.created(new URI("/admin/price-list/" + result.getId())).body(result);
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getPriceList(@PathVariable Long id) {
        Optional<PriceList> order = priceListRepository.findById(id);
        return order.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            path = "/{id}/addPrice",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> addPrice(@PathVariable Long id, @Valid @RequestBody Price price) {
        PriceList priceList = priceListRepository.findPriceListById(id);

        priceList.getPrices().add(price);

        PriceList result = priceListRepository.save(priceList);

        return ResponseEntity.accepted().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deletePriceList(@PathVariable Long id) {
        log.info("Request to delete order: {}", priceListRepository.findPriceListById(id));
        priceListRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // - PRICES Part
    @RequestMapping(
            path = "/price",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Price> prices() {
        return priceRepository.findAll();
    }

    @RequestMapping(
            path = "/price",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Price> createPrice(@Valid @RequestBody Price price) throws URISyntaxException {
        log.info("Request to create price: {}", price);

        Price result = priceRepository.save(price);

        return ResponseEntity.created(new URI("/admin/price-list/price/" + result.getId())).body(result);
    }

    @RequestMapping(
            path = "/price/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getPrice(@PathVariable Long id) {
        Optional<Price> order = priceRepository.findById(id);
        return order.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // - CURRENCY Part
    @RequestMapping(
            path = "/currency",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Currency> currencies() {
        return currencyRepository.findAll();
    }

    @RequestMapping(
            path = "/currency",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Currency> createCurrency(@Valid @RequestBody Currency currency) throws URISyntaxException {
        log.info("Request to create currency: {}", currency);

        Currency result = currencyRepository.save(currency);

        return ResponseEntity.created(new URI("/admin/price-list/currency/" + result.getId())).body(result);
    }

    @RequestMapping(
            path = "/currency/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getCurrency(@PathVariable Long id) {
        Optional<Currency> currency = currencyRepository.findById(id);
        return currency.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
