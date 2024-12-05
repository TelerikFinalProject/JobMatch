package com.telerikacademy.web.jobmatch.services.security;

import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.UserInfoMapper;
import com.telerikacademy.web.jobmatch.models.RefreshTokenEntity;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.AuthResponseDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.models.enums.TokenType;
import com.telerikacademy.web.jobmatch.repositories.contracts.RefreshTokenRepository;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationRestService {

    private final UserService userService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmployersService employersService;
    private final ProfessionalService professionalService;
    private final UserInfoMapper userInfoMapper;

    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            UserPrincipal userPrinciple = userService.findByUsername(authentication.getName());

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            saveUserRefreshToken(userPrinciple, refreshToken);
            createRefreshTokenCookie(response, refreshToken);

            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", userPrinciple.getUsername());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(24 * 60 * 60)
                    .username(userPrinciple.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (Exception e) {
            log.error("[AuthenticationRestService:userSignInAuth] Error getting access token", e);
            throw new UsernameNotFoundException("Error getting access token");
        }
    }

    private void saveUserRefreshToken(UserPrincipal userPrinciple, String refreshToken) {
        refreshTokenRepository.deleteAllByUserId(userPrinciple.getId());


        var refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userPrinciple)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    private Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }

    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {

        if(!authorizationHeader.startsWith(TokenType.Bearer.name())){
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        //Find refreshToken from database and should not be revoked : Same thing can be done through filter.
        var refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(tokens-> !tokens.isRevoked())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));

        UserPrincipal userPrincipal = refreshTokenEntity.getUser();

        //Now create the Authentication object
        Authentication authentication =  createAuthenticationObject(userPrincipal);

        //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role.
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return  AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .username(userPrincipal.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

    private static Authentication createAuthenticationObject(UserPrincipal userInfoEntity) {
        // Extract user details from UserDetailsEntity
        String username = userInfoEntity.getUsername();
        String password = userInfoEntity.getPassword();
        String roles = userInfoEntity.getRoles();

        // Extract authorities from roles (comma-separated)
        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    public AuthResponseDto registerEmployer(@Valid EmployerDtoIn employerDtoIn, HttpServletResponse response) {
        boolean isUsernameExist = true;
        boolean isEmailExist = true;
        try {
            userService.findByUsername(employerDtoIn.getUsername());
        } catch (EntityNotFoundException e) {
            isUsernameExist = false;
        }

        try {
            userService.findByEmail(employerDtoIn.getEmail());
        } catch (EntityNotFoundException e) {
            isEmailExist = false;
        }

        if (isUsernameExist) {
            throw new EntityDuplicateException("This username is already taken");
        }
        if (isEmailExist) {
            throw new EntityDuplicateException("This email is already taken");
        }

        UserPrincipal userDetailsEntity = userInfoMapper.convertToEmployerEntity(employerDtoIn);
        Authentication authentication = createAuthenticationObject(userDetailsEntity);

        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);


        employersService.createEmployer(employerDtoIn);
        int employerId = userService.findByUsername(employerDtoIn.getUsername()).getId();
        userDetailsEntity.setId(employerId);
        saveUserRefreshToken(userDetailsEntity,refreshToken);

        createRefreshTokenCookie(response,refreshToken);

            log.info("[AuthService:registerUser] User:{} Successfully registered", userDetailsEntity.getUsername());
            return   AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .username(userDetailsEntity.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();
    }

    public AuthResponseDto registerProfessional(@Valid ProfessionalDtoIn professionalDtoIn, HttpServletResponse response) {
        boolean isUsernameExist = true;
        boolean isEmailExist = true;
        try {
            userService.findByUsername(professionalDtoIn.getUsername());
        } catch (EntityNotFoundException e) {
            isUsernameExist = false;
        }

        try {
            userService.findByEmail(professionalDtoIn.getEmail());
        } catch (EntityNotFoundException e) {
            isEmailExist = false;
        }

        if (isUsernameExist) {
            throw new EntityDuplicateException("This username is already taken");
        }
        if (isEmailExist) {
            throw new EntityDuplicateException("This email is already taken");
        }

        UserPrincipal userDetailsEntity = userInfoMapper.convertToProfessionalEntity(professionalDtoIn);
        Authentication authentication = createAuthenticationObject(userDetailsEntity);

        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);


        professionalService.registerProfessional(professionalDtoIn);
        int id = userService.findByUsername(professionalDtoIn.getUsername()).getId();
        userDetailsEntity.setId(id);
        saveUserRefreshToken(userDetailsEntity,refreshToken);

        createRefreshTokenCookie(response,refreshToken);

        log.info("[AuthService:registerUser] User:{} Successfully registered", userDetailsEntity.getUsername());
        return   AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .username(userDetailsEntity.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

}
