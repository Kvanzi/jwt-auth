package com.kvanzi.jwtauth.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(
        min = 3,
        max = 16,
        message = "Username must be between {min} and {max} characters"
)
@Pattern(
        regexp = "^[a-zA-Z0-9_.-]+$",
        message = "Username can only contain letters, numbers, dots, hyphens, and underscores"
)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {

    String message() default "Username not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
