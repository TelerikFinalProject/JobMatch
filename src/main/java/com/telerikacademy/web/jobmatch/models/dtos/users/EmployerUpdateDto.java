package com.telerikacademy.web.jobmatch.models.dtos.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerUpdateDto extends EmployerDtoIn{
    private String currentPassword;
}
