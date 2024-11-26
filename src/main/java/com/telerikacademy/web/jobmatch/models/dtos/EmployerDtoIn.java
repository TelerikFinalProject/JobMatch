package com.telerikacademy.web.jobmatch.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerDtoIn {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String companyName;
    private String description;
    private String locCountryIsoCode;
    private int locCityId;
}
