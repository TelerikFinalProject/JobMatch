package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;

import java.util.Optional;

public interface UserRepository {
    UserPrincipal findByUsername(String username);

    UserPrincipal findByEmail(String email);

    UserPrincipal findById(int id);
}
