package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.models.dtos.users.AuthResponseDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.services.security.AuthenticationRestService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationRestController {

    private final AuthenticationRestService authenticationService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.getJwtTokensAfterAuthentication(authentication, response));
    }

    @PostMapping("/sign-up/employer")
    public ResponseEntity<?> registerEmployer(@Valid @RequestBody EmployerDtoIn employerDtoIn,
                                              BindingResult bindingResult,
                                              HttpServletResponse response) {
        log.info("[AuthController:registerUser]Signup Process Started for user:{}", employerDtoIn.getUsername());

        if (bindingResult.hasErrors()) {
            // Collect validation errors
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                        }
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errorMessages); // Return errors as response
        }

        try {
            ResponseEntity<AuthResponseDto> authResponse = ResponseEntity.ok(authenticationService.registerEmployer(employerDtoIn, response));
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse.getBody());
        } catch (Exception e) {
            log.error("[AuthController:registerEmployer]Signup Process Failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/sign-up/professional")
    public ResponseEntity<?> registerProfessional(@Valid @RequestBody ProfessionalDtoIn professionalDtoIn,
                                              BindingResult bindingResult,
                                              HttpServletResponse response) {
        log.info("[AuthController:registerUser]Signup Process Started for user:{}", professionalDtoIn.getUsername());

        if (bindingResult.hasErrors()) {
            // Collect validation errors
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                        }
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errorMessages); // Return errors as response
        }

        try {
            ResponseEntity<AuthResponseDto> authResponse = ResponseEntity.ok(authenticationService.registerProfessional(professionalDtoIn, response));
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse.getBody());
        } catch (Exception e) {
            log.error("[AuthController:registerEmployer]Signup Process Failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @PostMapping("/sign-up")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody EmployerDtoIn userRegistrationDto,
//                                          BindingResult bindingResult, HttpServletResponse httpServletResponse){
//
//        log.info("[AuthController:registerUser]Signup Process Started for user:{}",userRegistrationDto.getUsername());
//        if (bindingResult.hasErrors()) {
//            List<String> errorMessage = bindingResult.getAllErrors().stream()
//                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                    .toList();
//            log.error("[AuthController:registerUser]Errors in user:{}",errorMessage);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
//        }
//        return ResponseEntity.ok(authenticationService.registerUser(userRegistrationDto,httpServletResponse));
//    }

    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(authenticationService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }
}
