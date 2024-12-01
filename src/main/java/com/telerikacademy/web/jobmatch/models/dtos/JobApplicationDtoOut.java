package com.telerikacademy.web.jobmatch.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobApplicationDtoOut {
    private String description;
    private String firstName;
    private String lastName;
    private String email;
    private double minSalary;
    private double maxSalary;
    private String location;
    private String status;
    private Set<JobAd> matchedJobAds;
    private Set<Skill> qualifications;
    private boolean isHybrid;
}
