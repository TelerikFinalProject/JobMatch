package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.PasswordChangeDto;

public interface UserService {
    UserPrincipal findByUsername(String username);

    UserPrincipal findByEmail(String email);

    UserPrincipal findById(int id);

    void uploadImageUrl(int id, String url);

    void changePassword(UserPrincipal user, PasswordChangeDto passwordChangeDto);
}
