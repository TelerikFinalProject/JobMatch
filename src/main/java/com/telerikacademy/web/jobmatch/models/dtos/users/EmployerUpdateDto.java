package com.telerikacademy.web.jobmatch.models.dtos.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerUpdateDto extends EmployerDtoIn{
    @NotNull(message = "Current password" + NULL_FIELD_ERROR)
    @Size(min = 4)
    private String currentPassword;
}
