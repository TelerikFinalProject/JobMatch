package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.annotations.PasswordMatches;
import com.telerikacademy.web.jobmatch.models.dtos.users.UserDtoIn;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserDtoIn> {
    @Override
    public boolean isValid(UserDtoIn userDtoIn, ConstraintValidatorContext constraintValidatorContext) {
        if (userDtoIn == null) {
            return true;
        }

        return userDtoIn.getPassword().equals(userDtoIn.getConfirmPassword());
    }
}
