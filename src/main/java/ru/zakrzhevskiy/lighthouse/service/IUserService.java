package ru.zakrzhevskiy.lighthouse.service;

import ru.zakrzhevskiy.lighthouse.config.UserAlreadyExistException;
import ru.zakrzhevskiy.lighthouse.model.MyUserDetails;
import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.model.VerificationToken;

public interface IUserService {

    User registerNewUserAccount(User userDto)
            throws UserAlreadyExistException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    String validateVerificationToken(String token);

    String fulfillUserDetails(String token, MyUserDetails userDetails);
}