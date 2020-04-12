package ru.zakrzhevskiy.lighthouse.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//import ru.zakrzhevskiy.lighthouse.model.User;
import ru.zakrzhevskiy.lighthouse.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ru.zakrzhevskiy.lighthouse.model.User> user = userRepository.findUserByUsername(username);

        if (user.isPresent()) {
            ru.zakrzhevskiy.lighthouse.model.User realUser = user.get();
            return new User(realUser.getUsername(), realUser.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
//        return user.map(realUser -> new User(realUser.getEMail(), realUser.getPassword(), new ArrayList<>()))
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
