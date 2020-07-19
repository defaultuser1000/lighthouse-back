package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.price.Scanner;
import ru.zakrzhevskiy.lighthouse.repository.ScannerRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/admin/settings/scanner")
public class AdminScannerController {

    private final Logger log = LoggerFactory.getLogger(AdminScannerController.class);
    @Autowired
    private ScannerRepository scannerRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Scanner> scanners(
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "modificationDate") String sortBy
    ) {
        return scannerRepository.findAll(Sort.by(direction, sortBy));
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getScanner(@PathVariable Long id) {
        Optional<Scanner> scanner = scannerRepository.findById(id);
        return scanner.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Scanner> createScanner(@Valid @RequestBody Scanner scanner) throws URISyntaxException {
        log.info("Request to create scanner: {}", scanner);

        Scanner result = scannerRepository.save(scanner);

        return ResponseEntity.created(new URI("/admin/settings/scanner/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateScanner(@PathVariable Long id, @Valid @RequestBody Scanner scanner) {
        Scanner baseScanner = scannerRepository.findById(id).get();
        log.info("Request to update scanner: {}", baseScanner);
        Scanner result = scannerRepository.save(scanner);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteScanner(@PathVariable Long id) {
        log.info("Request to delete scanner: {}", scannerRepository.findById(id).get());
        scannerRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

}
