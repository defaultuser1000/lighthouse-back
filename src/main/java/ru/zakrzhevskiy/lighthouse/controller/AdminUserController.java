package ru.zakrzhevskiy.lighthouse.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.views.View;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView({View.OrderUser.class})
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.info("Request to create user: {}", user);

        User result = userRepository.save(user);

        return ResponseEntity.created(new URI("/users/user/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView({View.OrderUser.class})
    public ResponseEntity<?> users(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "modificationDate") String sortBy
    ) {
        Page<User> usersPage = userRepository.findAll(PageRequest.of(page, pageSize, Sort.by(direction, sortBy)));
        return ResponseEntity.ok().body(usersPage);
    }

    @RequestMapping(
            path = "/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView({View.Short.class})
    public ResponseEntity<?> getUsersByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(userRepository.findAllById(ids));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/user/{id}"
    )
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Request to delete user: {}", userRepository.findUserById(id));
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView({View.OrderUser.class})
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            path = "/user/{id}/ownedOrders",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @JsonView({View.OrderUser.class})
    public Iterable<Order> getUserOwnedOrders(@PathVariable Long id) {
        return userRepository.findUserById(id).getOwnedOrders();
    }

    @RequestMapping(
            path = "/user/{id}/createdOrders",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @JsonView({View.OrderUser.class})
    public Iterable<Order> getUserCreatedOrders(@PathVariable Long id) {
        return userRepository.findUserById(id).getCreatedOrders();
    }
}
