package com.telerikacademy.web.jobmatch.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobAdDtoIn {

    public static final String NULL_FIELD_ERROR = " should not be null!";

    @NotNull
    private String positionTitle;

    @NotNull
    private double minSalary;

    @NotNull
    private double maxSalary;

    @NotNull
    private String jobDescription;

    @NotNull
    private boolean hybrid;

    @NotNull(message = "Country Iso code" + NULL_FIELD_ERROR)
    @Size(min = 2, max = 3)
    private String locCountryIsoCode;

    @NotNull(message = "City ID" + NULL_FIELD_ERROR)
    private int locCityId;
}
