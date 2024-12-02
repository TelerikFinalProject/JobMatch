package com.telerikacademy.web.jobmatch.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobApplicationDtoOut {
    private int id;
    private String description;
    private String firstName;
    private String lastName;
    private String email;
    private double minSalary;
    private double maxSalary;
    private String location;
    private String status;
    private Set<JobAdDtoOut> matchedJobAds;
    private boolean isHybrid;
    private String skills;
}
