package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;

public interface UserRepository {
    UserPrincipal findByUsername(String username);

    UserPrincipal findByEmail(String email);

    UserPrincipal findById(int id);
}
