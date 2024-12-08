package com.telerikacademy.web.jobmatch.services;


import com.telerikacademy.web.jobmatch.exceptions.AuthorizationException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.repositories.contracts.UserRepository;
import com.telerikacademy.web.jobmatch.services.contracts.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final String USERNAME_OR_PASSWORD_IS_INCORRECT = "Username or password is incorrect";
    private final UserRepository userRepository;

    @Override
    public UserPrincipal login(String username, String password) {
        UserPrincipal userPrincipal = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USERNAME_OR_PASSWORD_IS_INCORRECT));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(password, userPrincipal.getPassword())) {
            throw new EntityNotFoundException(USERNAME_OR_PASSWORD_IS_INCORRECT);
        }

        return userPrincipal;
    }
}
