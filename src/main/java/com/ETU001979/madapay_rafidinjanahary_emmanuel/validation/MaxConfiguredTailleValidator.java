package com.ETU001979.madapay_rafidinjanahary_emmanuel.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MaxConfiguredTailleValidator implements ConstraintValidator<MaxConfiguredTaille, Integer> {
    private final int tailleMax;

    public MaxConfiguredTailleValidator(
            @Value("${madapay.pagination.taille-max:100}") int tailleMax
    ) {
        this.tailleMax = tailleMax;
    }

    @Override
    public boolean isValid(Integer taille, ConstraintValidatorContext context) {
        if (taille == null || taille <= tailleMax) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "La taille maximale est " + tailleMax
        ).addConstraintViolation();
        return false;
    }
}
