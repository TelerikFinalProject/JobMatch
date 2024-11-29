package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalDtoIn;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoMapper {

    private final PasswordEncoder passwordEncoder;
    public UserPrincipal convertToEmployerEntity(EmployerDtoIn userRegistrationDto) {
        UserPrincipal userInfoEntity = new UserPrincipal();
        userInfoEntity.setUsername(userRegistrationDto.getUsername());
        userInfoEntity.setEmail(userRegistrationDto.getUsername());
        userInfoEntity.setRoles("ROLE_EMPLOYER");
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        return userInfoEntity;
    }

    public UserPrincipal convertToProfessionalEntity(ProfessionalDtoIn userRegistrationDto) {
        UserPrincipal userInfoEntity = new UserPrincipal();
        userInfoEntity.setUsername(userRegistrationDto.getUsername());
        userInfoEntity.setEmail(userRegistrationDto.getUsername());
        userInfoEntity.setRoles("ROLE_PROFESSIONAL");
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        return userInfoEntity;
    }
}
