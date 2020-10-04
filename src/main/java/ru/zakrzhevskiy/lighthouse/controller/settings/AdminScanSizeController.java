package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.price.ScanSize;
import ru.zakrzhevskiy.lighthouse.repository.ScanSizeRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/settings/scan-size")
public class AdminScanSizeController {

    private final Logger log = LoggerFactory.getLogger(AdminScanSizeController.class);
    @Autowired
    private ScanSizeRepository scanSizeRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<ScanSize> scanSizes() {
        return scanSizeRepository.findAll();
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getScanSize(@PathVariable Long id) {
        Optional<ScanSize> scanSize = scanSizeRepository.findById(id);
        return scanSize.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ScanSize> createScanSize(@Valid @RequestBody ScanSize scanSize) throws URISyntaxException {
        log.info("Request to create scan size: {}", scanSize);

        ScanSize result = scanSizeRepository.save(scanSize);

        return ResponseEntity.created(new URI("/admin/settings/scan-size/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateScanSize(@PathVariable Long id, @Valid @RequestBody ScanSize scanSize) {
        ScanSize baseScanSize = scanSizeRepository.findById(id).get();
        log.info("Request to update scanSize: {}", baseScanSize);
        ScanSize result = scanSizeRepository.save(scanSize);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteScanSize(@PathVariable Long id) {
        log.info("Request to delete scanSize: {}", scanSizeRepository.findById(id).get());
        scanSizeRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

}
