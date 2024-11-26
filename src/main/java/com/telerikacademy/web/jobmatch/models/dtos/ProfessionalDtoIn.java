package com.telerikacademy.web.jobmatch.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessionalDtoIn {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String summary;
    private String locCountryIsoCode;
    private int locCityId;
}
