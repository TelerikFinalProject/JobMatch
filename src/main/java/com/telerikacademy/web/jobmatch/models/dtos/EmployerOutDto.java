package com.telerikacademy.web.jobmatch.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerOutDto {
    private String username;
    private String companyName;
    private String description;
    private String email;
    private String role;
    private String location;
}
