package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
