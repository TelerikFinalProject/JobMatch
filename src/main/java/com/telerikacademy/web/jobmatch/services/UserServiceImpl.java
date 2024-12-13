package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.PasswordChangeDto;
import com.telerikacademy.web.jobmatch.repositories.contracts.UserRepository;
import com.telerikacademy.web.jobmatch.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserPrincipal findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    public UserPrincipal findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public UserPrincipal findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public void uploadImageUrl(int id, String url) {
        UserPrincipal user = findById(id);

        user.setProfilePictureUrl(url);

        userRepository.save(user);
    }

    @Override
    public void changePassword(UserPrincipal user, PasswordChangeDto passwordChangeDto) {
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getPassword()));
        userRepository.save(user);
    }
}
