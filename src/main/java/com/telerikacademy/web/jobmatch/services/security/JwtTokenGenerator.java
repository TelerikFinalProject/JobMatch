package com.telerikacademy.web.jobmatch.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenGenerator {

    private final JwtEncoder jwtEncoder;

    public String generateAccessToken(Authentication authentication) {
        log.info("[JwtTokenGenerator:generateAccessToken] Token Creation Started for:{}", authentication.getName());

        String roles = getRolesOfUser(authentication);

        String permissions = getPermissionsFromRoles(roles);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("com.jobMatch.auth")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("scope", permissions)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(Authentication authentication) {

        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for:{}", authentication.getName());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("com.jobMatch.auth")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15 , ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("scope", "REFRESH_TOKEN")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String getPermissionsFromRoles(String roles) {
        Set<String> permissions = new HashSet<>();

        if (roles.contains("ROLE_ADMIN")) {
            permissions.addAll(List.of(
                    "WELCOME:READ",
                    "EMPLOYER:APPROVE",
                    "PROFESSIONAL:APPROVE",
                    "EMPLOYER:BLOCK",
                    "PROFESSIONAL:BLOCK",
                    "DATA:DELETE",
                    "SKILL:MANAGE"
            ));
        }
        if (roles.contains("ROLE_EMPLOYER")) {
            permissions.addAll(List.of(
                    "WELCOME:READ",
                    "PROFILE:READ",
                    "PROFILE:WRITE",
                    "JOB_AD:READ",
                    "JOB_AD:WRITE",
                    "JOB_APPLICATION:READ",
                    "PROFESSIONAL_PROFILE:READ"
            ));
        }

        if (roles.contains("ROLE_PROFESSIONAL")) {
            permissions.addAll(List.of(
                    "WELCOME:READ",
                    "PROFILE:READ",
                    "PROFILE:WRITE",
                    "JOB_APPLICATION:READ",
                    "JOB_APPLICATION:WRITE",
                    "JOB_AD:READ",
                    "COMPANY_PROFILE:READ"
            ));
        }

        return String.join(" ", permissions);
    }

    private String getRolesOfUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}
