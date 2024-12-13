package com.telerikacademy.web.jobmatch.models.dtos.users.mvc;

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
public class PasswordChangeDto {
    public static final String NULL_FIELD_ERROR = " should not be null!";

    @NotNull(message = "Previous Password" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 64, message = "Please provide your previous password")
    private String previousPassword;

    @NotNull(message = "Password" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 64, message = "New password should be between 8 and 64 characters")
    private String password;

    @NotNull(message = "Confirm password" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 64, message = "New password should be between 8 and 64 characters")
    private String confirmPassword;
}
