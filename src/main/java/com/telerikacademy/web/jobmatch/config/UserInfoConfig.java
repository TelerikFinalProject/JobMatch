package com.telerikacademy.web.jobmatch.config;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class UserInfoConfig implements UserDetails {

    private final UserPrincipal userPrincipal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converts string of roles (coma separated) into a collection of SimpleGrantedAuthority objects
        // Represents the authorities granted to the user by spring security
        return Collections.singletonList(new SimpleGrantedAuthority(userPrincipal.getRole().getRole()));
    }

    @Override
    public String getPassword() {
        return userPrincipal.getPassword();
    }

    @Override
    public String getUsername() {
        return userPrincipal.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
