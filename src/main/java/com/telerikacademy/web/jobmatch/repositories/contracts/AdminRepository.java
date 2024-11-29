package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository {
    List<UserPrincipal> getAdmins();

    UserPrincipal getAdmin(int id);

    UserPrincipal getAdmin(String username);

    void createAdmin(UserPrincipal admin);

    void updateAdmin(UserPrincipal updatedUser);

    void deleteAdmin(UserPrincipal admin);
}
