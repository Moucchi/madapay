package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MaxConfiguredTailleValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxConfiguredTaille {
    String message() default "La taille dépasse la limite configurée.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
