package com.telerikacademy.web.jobmatch.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessionalUpdateDto extends ProfessionalDtoIn{
    private String currentPassword;
}
