package ru.zakrzhevskiy.lighthouse.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.zakrzhevskiy.lighthouse.model.MyUserDetails;
import ru.zakrzhevskiy.lighthouse.model.ReferencePhoto;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.VerificationToken;
import ru.zakrzhevskiy.lighthouse.model.views.View;
import ru.zakrzhevskiy.lighthouse.repository.OrderRepository;
import ru.zakrzhevskiy.lighthouse.repository.ReferencePhotosRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;
import ru.zakrzhevskiy.lighthouse.repository.VerificationTokenRepository;
import ru.zakrzhevskiy.lighthouse.service.OnRegistrationCompleteEvent;
import ru.zakrzhevskiy.lighthouse.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReferencePhotosRepository referencePhotosRepository;

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<?> signUp(@Valid @RequestBody User user, HttpServletRequest request) {

        if (usernameExist(user.getUsername())) {
            return ResponseEntity.status(CONFLICT).body("Username");
        } else if (emailExist(user.getEMail())) {
            return ResponseEntity.status(CONFLICT).body("E-Mail");
        } else {
            User result = userService.registerNewUserAccount(user);
            eventPublisher.publishEvent(
                    new OnRegistrationCompleteEvent(result, request.getLocale(), request.getContextPath())
            );
            VerificationToken token = tokenRepository.findByUser(result);

            return ResponseEntity.status(CREATED).body(token.getToken());
        }

    }
    private boolean usernameExist(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }
    private boolean emailExist(String email) {
        return userRepository.findUserByeMail(email).isPresent();
    }

    @SneakyThrows
    @RequestMapping(method = RequestMethod.GET, path = "/registrationConfirm")
    public void registrationConfirm(@RequestParam("token") final String token, HttpServletResponse resp) {

        final String result = userService.validateVerificationToken(token);

        if (result.equals("valid")) {
            resp.sendRedirect("/activationSuccess");
        } else {
            resp.setStatus(500);
            resp.sendRedirect("/errorActivating?error=" + result);
        }
    }

    @SneakyThrows
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/setUserDetails",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void updateUserDetails(@RequestParam(name = "token") String token, @Valid @RequestBody MyUserDetails userDetails, HttpServletResponse resp) {

        final String result = userService.fulfillUserDetails(token, userDetails);

        if (!result.equals("userDetailsUpdated")) {
            resp.sendError(500, "Failed to set user details");
        }
    }

    @RequestMapping(
            path = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @JsonView(View.Short.class)
    public ResponseEntity<?> authenticate(Principal principal) {
        Optional<User> user = userRepository.findUserByUsername(principal.getName());

        return user.map(ResponseEntity::ok).orElse(ResponseEntity.status(UNAUTHORIZED).build());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/check-auth",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> checkAuth(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @SneakyThrows
    @Transactional
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getUserProfile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getProfile(Principal principal) {
        return userRepository.findUserByUsername(principal.getName())
                .map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SneakyThrows
    @RequestMapping(
            path = "/user/{id}/uploadAvatar",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadUserAvatar(@RequestParam MultipartFile avatar, @PathVariable Long id) {
        User user = userRepository.findUserById(id);

        if (user.getMyUserDetails() == null) {
            MyUserDetails details = new MyUserDetails();
            details.setAvatar(avatar.getBytes());

            user.setMyUserDetails(details);
        } else {
            user.getMyUserDetails().setAvatar(avatar.getBytes());
        }

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/user/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User baseUser = userRepository.findUserById(id);
        log.info("Request to update user: {}", baseUser);
        user.setPassword(baseUser.getPassword());
        User result = userRepository.save(user);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(
            path = "/user/{id}/referencePhotos",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView(View.Short.class)
    public ResponseEntity<?> addReferencePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile multipartFile, @RequestParam("thumbnail") MultipartFile thumbnail, @RequestParam("description") String description) throws IOException {
        log.info("Request to add user {} reference photo", id);

        ReferencePhoto referencePhoto = new ReferencePhoto();
        referencePhoto.setPhoto(multipartFile.getBytes());
        referencePhoto.setThumbnail(thumbnail.getBytes());
        referencePhoto.setDescription(description);
        referencePhoto.setUserId(id);

        ReferencePhoto result = referencePhotosRepository.save(referencePhoto);

        return ResponseEntity.status(CREATED).body(result);
    }

    @SneakyThrows
    @RequestMapping(
            path = "/user/{id}/referencePhotos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @JsonView(View.Short.class)
    public ResponseEntity<?> getUserReferencePhotos(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "creationDate") String sortBy
    ) {
        Page<ReferencePhoto> referencePhotoPage = referencePhotosRepository.findByUserId(id, PageRequest.of(page, pageSize, Sort.by(direction, sortBy)));

        return ResponseEntity.ok(referencePhotoPage);
    }

    @SneakyThrows
    @RequestMapping(
            path = "/user/referencePhotos/{photoId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @JsonView(View.Full.class)
    public ResponseEntity<?> getUserReferencePhoto(@PathVariable(name = "photoId") Long photoId) {

        Optional<ReferencePhoto> referencePhoto = referencePhotosRepository.findById(photoId);

        return referencePhoto.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            path = "/user/referencePhotos/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> editReferencePhotoDescription(@PathVariable Long id, @RequestParam(name = "description") String description) {
        Optional<ReferencePhoto> referencePhoto = referencePhotosRepository.findById(id);

        return referencePhoto.map(photo -> {
            photo.setDescription(description);
            referencePhotosRepository.save(photo);
            return ResponseEntity.ok().body(photo);
        }).orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/user/referencePhotos/{id}"
    )
    @Transactional
    public ResponseEntity<?> deleteReferencePhoto(@PathVariable Long id) {
        log.info("Request to delete referencePhoto: {}", referencePhotosRepository.findById(id));
        referencePhotosRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/acceptTnC")
    @Transactional
    public ResponseEntity<?> acceptTermsOfConditions(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(RuntimeException::new);

        user.setTermsOfConditionsAccepted(true);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
