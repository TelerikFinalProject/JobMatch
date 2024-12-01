package com.telerikacademy.web.jobmatch.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessionalOutDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String summary;
    private String roles;
    private String location;
    private String status;
    private String successfulMatches;
}
