package ru.zakrzhevskiy.lighthouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.zakrzhevskiy.lighthouse.config.UserAlreadyExistException;
import ru.zakrzhevskiy.lighthouse.model.MyUserDetails;
import ru.zakrzhevskiy.lighthouse.model.Role;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.VerificationToken;
import ru.zakrzhevskiy.lighthouse.repository.RoleRepository;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;
import ru.zakrzhevskiy.lighthouse.repository.VerificationTokenRepository;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserService implements IUserService {

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    public static final String USER_DETAILS_UPDATED = "userDetailsUpdated";

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User registerNewUserAccount(User user)
            throws UserAlreadyExistException {

        if (emailExist(user.getEMail())) {
            throw new UserAlreadyExistException(
                    "There is an account with that email adress: "
                            + user.getEMail());
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("USER"));
        user.setRoles(roles);
        user.setMyUserDetails(null);

        return repository.save(user);
    }

    private boolean emailExist(String email) {
        return repository.findUserByeMail(email).isPresent();
    }

    @Override
    public User getUser(String verificationToken) {
        return tokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    public VerificationToken getVerificationToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        repository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        repository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public String fulfillUserDetails(String token, MyUserDetails userDetails) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            return TOKEN_EXPIRED;
        }

        user.setMyUserDetails(userDetails);
        repository.save(user);
        return USER_DETAILS_UPDATED;
    }
}