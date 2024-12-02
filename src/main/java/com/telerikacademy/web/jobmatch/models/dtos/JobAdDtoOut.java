package com.telerikacademy.web.jobmatch.models.dtos;

import com.telerikacademy.web.jobmatch.models.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobAdDtoOut {
    private int id;
    private String positionTitle;
    private Double minSalary;
    private Double maxSalary;
    private String jobDescription;
    private String companyName;
    private String city;
    private boolean hybrid;
    private String skills;
}
