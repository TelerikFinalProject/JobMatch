package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.UserDtoIn;

import java.util.List;

public interface AdminService {
    List<UserPrincipal> getAdmins();

    void createAdmin(UserDtoIn userDtoIn);

    void updateAdmin(UserPrincipal updatedUser);

    void deleteAdmin(int id);
}
