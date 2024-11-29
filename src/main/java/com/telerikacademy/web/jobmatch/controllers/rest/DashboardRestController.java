package com.telerikacademy.web.jobmatch.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardRestController {

    @PreAuthorize("hasAuthority('SCOPE_WELCOME:READ')")
    @GetMapping("/welcome-message")
    public ResponseEntity<String> getFirstWelcomeMessage(Authentication authentication) {
        return ResponseEntity.ok("Welcome to the JobMatch API " + authentication.getName()
                + " with scope: "
                + authentication.getAuthorities());
    }

    @PreAuthorize("hasAuthority('SCOPE_JOB_AD:WRITE')")
    @GetMapping("/employer-message")
    public ResponseEntity<String> getEmployerMessage(Authentication authentication) {
        return ResponseEntity.ok("Employer: " + authentication.getName());
    }

    @PreAuthorize("hasAuthority('SCOPE_JOB_APPLICATION:WRITE')")
    @GetMapping("/professional-message")
    public ResponseEntity<String> getProfessionalMessage(Authentication authentication) {
        return ResponseEntity.ok("Professional: " + authentication.getName());
    }
}
