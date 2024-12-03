package com.telerikacademy.web.jobmatch.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobApplicationDtoUpdate extends JobApplicationDtoIn{
    @NotNull(message = "Status" + NULL_FIELD_ERROR)
    private String status;
}
