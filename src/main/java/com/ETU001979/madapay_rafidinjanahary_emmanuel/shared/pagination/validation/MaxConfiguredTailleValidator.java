package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.validation;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.config.MadapayProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxConfiguredTailleValidator implements ConstraintValidator<MaxConfiguredTaille, Integer> {

    private final int tailleMax;

    public MaxConfiguredTailleValidator(MadapayProperties properties) {
        this.tailleMax = properties.pagination().tailleMax();
    }

    @Override
    public boolean isValid(Integer taille, ConstraintValidatorContext context) {
        if (taille == null || taille <= tailleMax) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("La taille maximale est " + tailleMax + ".")
                .addConstraintViolation();
        return false;
    }
}
