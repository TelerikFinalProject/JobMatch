package com.telerikacademy.web.jobmatch.config;

import com.telerikacademy.web.jobmatch.models.enums.TokenType;
import com.telerikacademy.web.jobmatch.records.RSAKeyRecord;
import com.telerikacademy.web.jobmatch.utils.JwtTokenUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: Started ");

            log.info("[JwtAccessTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (request.getRequestURI().startsWith("/api/locations")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();

                final String token = authHeader.substring(TokenType.Bearer.name().length() + 1);
                final Jwt jwtToken = jwtDecoder.decode(token);

                final String username = jwtTokenUtils.getUsername(jwtToken);

                if (!username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = jwtTokenUtils.mapToUserDetails(username);

                    if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)) {
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                        UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                        createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        securityContext.setAuthentication(createdToken);
                        SecurityContextHolder.setContext(securityContext);
                    }
                }

                log.info("[JwtAccessTokenFilter:doFilterInternal] Completed");

                filterChain.doFilter(request, response);
            }
        } catch (JwtValidationException jwtValidationException) {
            log.error("[JwtAccessTokenFilter:doFilterInternal] JWT validation failed: {}", jwtValidationException.getMessage());
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT validation failed: " + jwtValidationException.getMessage() + "\"}");
            response.getWriter().flush();
        }
    }
}
