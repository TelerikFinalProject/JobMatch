package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;

public interface UserService {
    UserPrincipal findByUsername(String username);

    UserPrincipal findByEmail(String email);

    UserPrincipal findById(int id);

    void uploadImageUrl(int id, String url);
}
