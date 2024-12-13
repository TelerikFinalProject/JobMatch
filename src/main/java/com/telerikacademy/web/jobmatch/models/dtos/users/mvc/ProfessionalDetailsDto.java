package com.telerikacademy.web.jobmatch.models.dtos.users.mvc;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessionalDetailsDto {
    public static final String NULL_FIELD_ERROR = " should not be null!";

    @NotNull(message = "Username" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32, message = "Username must be between 4 and 32 characters.")
    private String username;

    @NotNull(message = "Email" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 32, message = "Email must be between 8 and 32 characters.")
    private String email;

    @NotNull(message = "Country Iso code" + NULL_FIELD_ERROR)
    @Size(min = 2, max = 3, message = "Iso code must be between 2 and 3 characters.")
    private String locCountryIsoCode;

    @NotNull(message = "City ID" + NULL_FIELD_ERROR)
    private Integer locCityId;

    @NotNull(message = "First name" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32, message = "First name must be between 3 and 32 characters.")
    private String firstName;

    @NotNull(message = "Last name" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32, message = "Last name must be between 3 and 32 characters.")
    private String lastName;

    @NotNull(message = "Summary" + NULL_FIELD_ERROR)
    @Size(min = 10, max = 500, message = "Summary must be between 10 and 500 characters.")
    private String summary;
}
