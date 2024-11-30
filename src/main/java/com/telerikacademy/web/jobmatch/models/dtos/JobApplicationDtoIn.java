package com.telerikacademy.web.jobmatch.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobApplicationDtoIn {
    public static final String NULL_FIELD_ERROR = " should not be null!";

    String description;
    double minSalary;
    double maxSalary;
    @NotNull(message = "Country Iso code" + NULL_FIELD_ERROR)
    @Size(min = 2, max = 3)
    private String locCountryIsoCode;

    @NotNull(message = "City ID" + NULL_FIELD_ERROR)
    private int locCityId;

    boolean isHybrid;
}