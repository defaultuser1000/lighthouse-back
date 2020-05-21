package ru.zakrzhevskiy.lighthouse.controller;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.*;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.RoleRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> signUp(@Valid @RequestBody User user) {

        if (usernameExist(user.getUsername())) {
            return ResponseEntity.status(CONFLICT).body("Username");
        } else if (emailExist(user.getEMail())) {
            return ResponseEntity.status(CONFLICT).body("E-Mail");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("USER"));
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.status(CREATED).build();
        }

    }
    private boolean usernameExist(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }
    private boolean emailExist(String email) {
        return userRepository.findUserByeMail(email).isPresent();
    }

    @RequestMapping(
            path = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<?> authenticate(Principal principal) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());

        if (user.isPresent()) {
            MyUserDetails details = user.get().getMyUserDetails() == null ? new MyUserDetails() : user.get().getMyUserDetails();

            return ResponseEntity.ok(
                    new UserProfile(
                            user.get().getId(),
                            user.get().getUsername(),
                            !details.getFIO().contains("null") ? details.getFIO() : "",
                            details.getAvatar() != null ? details.getAvatar() : new byte[] {}
                            )
            );
        } else {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
    }

    @SneakyThrows
    @RequestMapping(
            path = "/user/{id}/uploadAvatar",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadUserAvatar(@RequestParam MultipartFile avatar, @PathVariable Long id) {
        User user = userRepository.findUserById(id);
        user.getMyUserDetails().setAvatar(avatar.getBytes());
        userRepository.save(user);
        return ResponseEntity.ok().build();
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
}
