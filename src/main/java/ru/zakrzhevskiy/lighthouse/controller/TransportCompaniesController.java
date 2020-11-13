package ru.zakrzhevskiy.lighthouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.zakrzhevskiy.lighthouse.model.TransportCompany;
import ru.zakrzhevskiy.lighthouse.repository.TransportCompanyRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/transportCompanies")
public class TransportCompaniesController {

    private final Logger log = LoggerFactory.getLogger(TransportCompaniesController.class);
    @Autowired
    private TransportCompanyRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getCompanies(HttpServletRequest request) {
        List<TransportCompany> allTransportCompanies = repository.findAll();

        List<TransportCompany> filteredCompanies = allTransportCompanies
                .stream()
                .filter(transportCompany -> transportCompany
                        .getCompanyNames()
                        .stream()
                        .anyMatch(name -> name.getLocale().equals(request.getLocale()))
                ).collect(toList());

        return ResponseEntity.ok(filteredCompanies);
    }
}
