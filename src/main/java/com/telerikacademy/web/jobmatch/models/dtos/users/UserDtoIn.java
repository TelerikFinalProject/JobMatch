package com.telerikacademy.web.jobmatch.models.dtos.users;

import com.telerikacademy.web.jobmatch.annotations.PasswordMatches;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@PasswordMatches
public class UserDtoIn {
    public static final String NULL_FIELD_ERROR = " should not be null!";

    @NotNull(message = "Username" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32, message = "Username should be between 4 and 32 characters long")
    private String username;

    @NotNull(message = "Email" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 32, message = "Email should be between 4 and 32 characters long")
    private String email;

    @NotNull(message = "Password" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 64, message = "Password should be between 4 and 32 characters long")
    private String password;

    @NotNull(message = "Confirm password" + NULL_FIELD_ERROR)
    private String confirmPassword;

    @NotNull(message = "Country Iso code" + NULL_FIELD_ERROR)
    @Size(min = 2, max = 3, message = "Please provide a country")
    private String locCountryIsoCode;

    @NotNull(message = "City" + NULL_FIELD_ERROR)
    private Integer locCityId;
}
