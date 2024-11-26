package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Role;

public interface RoleRepository {
    Role getRole(String roleName);
}
