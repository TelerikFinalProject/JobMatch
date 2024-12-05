package com.telerikacademy.web.jobmatch.models.dtos.filters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class JobApplicationFilterDto {
    private Double minSalary;
    private Double maxSalary;
    private String location;
    private String creator;
    private String status;
    private Boolean hybrid;
}
