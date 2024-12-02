package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoMapper {

    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;

    public UserPrincipal convertToEmployerEntity(EmployerDtoIn userRegistrationDto) {
        UserPrincipal userInfoEntity =
                EmployerMappers.INSTANCE.toUserPrinciple(userRegistrationDto, locationService);
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        return userInfoEntity;
    }

    public UserPrincipal convertToProfessionalEntity(ProfessionalDtoIn userRegistrationDto) {
        UserPrincipal userInfoEntity =
                ProfessionalMappers.INSTANCE.toUserPrinciple(userRegistrationDto, locationService);
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        return userInfoEntity;
    }
}
