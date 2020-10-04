package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.price.Process;
import ru.zakrzhevskiy.lighthouse.repository.ProcessRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/settings/process")
public class AdminProcessController {

    private final Logger log = LoggerFactory.getLogger(AdminProcessController.class);
    @Autowired
    private ProcessRepository processRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Process> processs() {
        return processRepository.findAll();
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getProcess(@PathVariable Long id) {
        Optional<Process> process = processRepository.findById(id);
        return process.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Process> createProcess(@Valid @RequestBody Process process) throws URISyntaxException {
        log.info("Request to create process: {}", process);

        Process result = processRepository.save(process);

        return ResponseEntity.created(new URI("/admin/settings/process/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateProcess(@PathVariable Long id, @Valid @RequestBody Process process) {
        Process baseProcess = processRepository.findById(id).get();
        log.info("Request to update process: {}", baseProcess);
        Process result = processRepository.save(process);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteProcess(@PathVariable Long id) {
        log.info("Request to delete process: {}", processRepository.findById(id).get());
        processRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

}
