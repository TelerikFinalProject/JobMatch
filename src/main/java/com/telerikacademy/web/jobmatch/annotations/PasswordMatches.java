package com.telerikacademy.web.jobmatch.annotations;

import com.telerikacademy.web.jobmatch.helpers.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Passwords must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
