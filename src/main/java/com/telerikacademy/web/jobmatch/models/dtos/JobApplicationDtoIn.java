package com.telerikacademy.web.jobmatch.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobApplicationDtoIn {
    public static final String NULL_FIELD_ERROR = " should not be null!";
    public static final String SALARY_ERROR = "Salary should always be positive!";

    @NotNull(message = "Description" + NULL_FIELD_ERROR)
    @Size(min = 10, max = 500)
    private String description;

    @NotNull(message = "Salary" + NULL_FIELD_ERROR)
    @Positive(message = SALARY_ERROR)
    private Double minSalary;

    @NotNull(message = "Salary" + NULL_FIELD_ERROR)
    @Positive(message = SALARY_ERROR)
    private Double maxSalary;

    private String locCountryIsoCode;

    private Integer locCityId;

    @NotNull(message = "Remote spec" + NULL_FIELD_ERROR)
    private boolean isHybrid;

    private String skills;
    private Boolean remote;
}