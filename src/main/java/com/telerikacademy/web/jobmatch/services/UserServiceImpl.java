package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.repositories.contracts.UserRepository;
import com.telerikacademy.web.jobmatch.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserPrincipal findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserPrincipal findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserPrincipal findById(int id) {
        return userRepository.findById(id);
    }
}