package com.telerikacademy.web.jobmatch.models.dtos.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessionalDtoIn extends UserDtoIn {
    @NotNull(message = "First name" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32)
    private String firstName;

    @NotNull(message = "Last name" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32)
    private String lastName;

    @NotNull(message = "Summary" + NULL_FIELD_ERROR)
    @Size(min = 10, max = 200)
    private String summary;
}
