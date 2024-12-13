package com.telerikacademy.web.jobmatch.helpers;

import com.telerikacademy.web.jobmatch.annotations.PasswordMatches;
import com.telerikacademy.web.jobmatch.models.dtos.users.UserDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.PasswordChangeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true; // Let null objects be handled by @NotNull annotations
        }

        boolean passwordsMatch = false;
        // Check if the object is of type PasswordChangeDto
        if (object instanceof PasswordChangeDto passwordChangeDto) {
            passwordsMatch = passwordChangeDto.getPassword().equals(passwordChangeDto.getConfirmPassword());
        }
        // Check if the object is of type UserDtoIn
        else if (object instanceof UserDtoIn userDtoIn) {
            passwordsMatch = userDtoIn.getPassword().equals(userDtoIn.getConfirmPassword());
        }

        if (!passwordsMatch) {
            // Link the violation to the "confirmPassword" field
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return passwordsMatch;
    }
}
