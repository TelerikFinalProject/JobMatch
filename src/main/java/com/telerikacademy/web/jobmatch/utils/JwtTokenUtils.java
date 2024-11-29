package com.telerikacademy.web.jobmatch.utils;

import com.telerikacademy.web.jobmatch.config.UserInfoConfig;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.repositories.contracts.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    public String getUsername(Jwt jwtToken){

        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails){

        final String username = getUsername(jwtToken);

        boolean isTokenExpired = getIfTokenIsExpired(jwtToken);

        boolean isTokenUserSameAsDatabase = username.equals(userDetails.getUsername());

        return !isTokenExpired  && isTokenUserSameAsDatabase;

    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {

        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    private final UserRepository userRepository;

    public UserDetails mapToUserDetails(String username){


        UserPrincipal user = userRepository.findByUsername(username);
        return new UserInfoConfig(user);
    }
}