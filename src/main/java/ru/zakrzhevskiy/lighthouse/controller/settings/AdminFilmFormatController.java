package ru.zakrzhevskiy.lighthouse.controller.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.price.FilmFormat;
import ru.zakrzhevskiy.lighthouse.repository.FilmFormatRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/settings/film-format")
public class AdminFilmFormatController {

    private final Logger log = LoggerFactory.getLogger(AdminFilmFormatController.class);
    @Autowired
    private FilmFormatRepository filmFormatRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<FilmFormat> filmFormats() {
        return filmFormatRepository.findAll();
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getFilmFormat(@PathVariable Long id) {
        Optional<FilmFormat> filmFormat = filmFormatRepository.findById(id);
        return filmFormat.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FilmFormat> createFilmFormat(@Valid @RequestBody FilmFormat filmFormat) throws URISyntaxException {
        log.info("Request to create film format: {}", filmFormat);

        FilmFormat result = filmFormatRepository.save(filmFormat);

        return ResponseEntity.created(new URI("/admin/settings/film-format/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateFilmFormat(@PathVariable Long id, @Valid @RequestBody FilmFormat filmFormat) {
        FilmFormat baseFilmFormat = filmFormatRepository.findById(id).get();
        log.info("Request to update film format: {}", baseFilmFormat);
        FilmFormat result = filmFormatRepository.save(filmFormat);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/{id}"
    )
    public ResponseEntity<?> deleteFilmFormat(@PathVariable Long id) {
        log.info("Request to delete film format: {}", filmFormatRepository.findById(id).get());
        filmFormatRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

}
