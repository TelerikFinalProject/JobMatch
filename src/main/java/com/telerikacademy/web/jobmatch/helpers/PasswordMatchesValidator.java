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

        boolean passwordsMatch =  userDtoIn.getPassword().equals(userDtoIn.getConfirmPassword());

        if (!passwordsMatch) {
            // Link the violation to the "confirmPassword" field
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("confirmPassword") // Link to "confirmPassword" field
                    .addConstraintViolation();
        }

        return passwordsMatch;
    }
}
