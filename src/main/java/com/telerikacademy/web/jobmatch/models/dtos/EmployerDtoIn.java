package com.telerikacademy.web.jobmatch.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerDtoIn extends UserDtoIn{
    @NotNull(message = "Company name" + NULL_FIELD_ERROR)
    @Size(min = 3, max = 32)
    private String companyName;

    @NotNull(message = "Description" + NULL_FIELD_ERROR)
    @Size(min = 10, max = 200)
    private String description;
}
