package ru.zakrzhevskiy.lighthouse.controller.settings;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.TransportCompany;
import ru.zakrzhevskiy.lighthouse.model.TransportCompanyName;
import ru.zakrzhevskiy.lighthouse.repository.TransportCompanyNamesRepository;
import ru.zakrzhevskiy.lighthouse.repository.TransportCompanyRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/settings/transport-companies")
public class AdminTransportCompanyController {

    private final Logger log = LoggerFactory.getLogger(AdminTransportCompanyController.class);
    @Autowired
    private TransportCompanyRepository companyRepository;
    @Autowired
    private TransportCompanyNamesRepository companyNamesRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<TransportCompany> transportCompanies() {
        return companyRepository.findAll();
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getTransportCompany(@PathVariable Long id) {
        Optional<TransportCompany> scanSize = companyRepository.findById(id);
        return scanSize.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TransportCompany> createTransportCompany(@Valid @RequestBody TransportCompany transportCompany, HttpServletRequest request) throws URISyntaxException {
        log.info("Request to create transport company: {}", transportCompany);

        List<TransportCompanyName> companyNames = transportCompany.getCompanyNames();
        companyNames.forEach(name -> name.setLocale(request.getLocale()));

        List<TransportCompanyName> savedCompanyNames = companyNamesRepository.saveAll(companyNames);
        transportCompany.setCompanyNames(savedCompanyNames);

        TransportCompany result = companyRepository.save(transportCompany);

        return ResponseEntity.created(new URI("/admin/settings/transport-company/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateTransportCompany(@PathVariable Long id, @Valid @RequestBody TransportCompany transportCompany) {
        TransportCompany baseTransportCompany = companyRepository.findById(id).get();
        log.info("Request to update Transport Company: {}", baseTransportCompany);
        TransportCompany result = companyRepository.save(transportCompany);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteTransportCompany(@PathVariable Long id) {
        log.info("Request to delete Transport Company: {}", companyRepository.findById(id).get());
        companyRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

}
