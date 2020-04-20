package ru.zakrzhevskiy.lighthouse.controller;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.Order;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.UserProfile;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.info("Request to create user: {}", user);

        User result = userRepository.save(user);

        return ResponseEntity.created(new URI("/users/user/" + result.getId())).body(result);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void signUp(@Valid @RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @RequestMapping(
            path = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    public UserProfile authenticate(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElse(null);
        return new UserProfile(user.getId(), user.getUsername(), user.getFIO(), user.getAvatar());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<User> users() {
        return userRepository.findAll();
    }

    @RequestMapping(
            path = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
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
    public Iterable<Order> getUserOwnedOrders(@PathVariable Long id) {
        return userRepository.findUserById(id).getOwnedOrders();
    }

    @RequestMapping(
            path = "/user/{id}/createdOrders",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Order> getUserCreatedOrders(@PathVariable Long id) {
        return userRepository.findUserById(id).getCreatedOrders();
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/user/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User baseUser = userRepository.findUserById(id);
        log.info("Request to update user: {}", baseUser);
        User result = userRepository.save(user);
        return ResponseEntity.ok().body(result);
    }

    @SneakyThrows
    @RequestMapping(
            path = "/user/{id}/uploadAvatar",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadUserAvatar(@RequestParam MultipartFile avatar, @PathVariable Long id) {
        User user = userRepository.findUserById(id);
        user.setAvatar(avatar.getBytes());
        userRepository.save(user);
        return ResponseEntity.ok().build();
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
}
