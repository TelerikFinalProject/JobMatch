package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;

public interface AuthenticationService {

    UserPrincipal login(String username, String password);
}
