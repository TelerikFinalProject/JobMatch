package com.telerikacademy.web.jobmatch.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoIn.SALARY_ERROR;

@Getter
@Setter
@NoArgsConstructor
public class JobAdDtoIn {

    public static final String NULL_FIELD_ERROR = " should not be null!";

    @NotNull(message = "Title" + NULL_FIELD_ERROR)
    @Size(min = 5, max = 40)
    private String positionTitle;

    @NotNull(message = "Salary" + NULL_FIELD_ERROR)
    @Positive(message = SALARY_ERROR)
    private double minSalary;

    @NotNull(message = "Salary" + NULL_FIELD_ERROR)
    @Positive(message = SALARY_ERROR)
    private double maxSalary;

    @NotNull(message = "Description" + NULL_FIELD_ERROR)
    @Size(min = 5, max = 200)
    private String jobDescription;

    @NotNull
    private boolean hybrid;

    @NotNull(message = "Country Iso code" + NULL_FIELD_ERROR)
    @Size(min = 2, max = 3)
    private String locCountryIsoCode;

    @NotNull(message = "City ID" + NULL_FIELD_ERROR)
    private int locCityId;

    private String skills;
}
