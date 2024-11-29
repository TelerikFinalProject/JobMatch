package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    @Query(value = "SELECT rt.* FROM refresh_tokens rt " +
            "INNER JOIN users_details ud ON rt.user_id = ud.id " +
            "WHERE ud.USERNAME = :username and rt.revoked = false ", nativeQuery = true)
    List<RefreshTokenEntity> findAllRefreshTokenByUsername(String username);
}
