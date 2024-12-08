package com.telerikacademy.web.jobmatch.models.dtos.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDto {

    public static final String NULL_FIELD_ERROR = " should not be null!";

    @NotNull(message = "Username" + NULL_FIELD_ERROR)
    @Size(min = 4, max = 32)
    private String username;

    @NotNull(message = "Password" + NULL_FIELD_ERROR)
    @Size(min = 8, max = 64)
    private String password;
}
