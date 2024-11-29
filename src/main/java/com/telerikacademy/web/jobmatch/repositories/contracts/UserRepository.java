package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Role;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserPrincipal, Integer> {
    Optional<UserPrincipal> findByUsername(String username);

    Optional<UserPrincipal> findByEmail(String email);

    @Query("select u from UserPrincipal u join u.role r where r.role = :role")
    List<UserPrincipal> findAllByRole(@Param("role") String name);
}
