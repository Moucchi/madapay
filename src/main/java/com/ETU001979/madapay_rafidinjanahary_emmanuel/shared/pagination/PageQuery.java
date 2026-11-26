package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.validation.MaxConfiguredTaille;
import jakarta.validation.constraints.Min;

/** Paramètres de pagination reçus en requête ({@code ?page=&taille=}), optionnels. */
public record PageQuery(
        @Min(value = 1, message = "La page minimale est 1.")
        Integer page,

        @Min(value = 1, message = "La taille minimale est 1.")
        @MaxConfiguredTaille
        Integer taille
) {
}
