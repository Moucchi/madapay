package com.ETU001979.madapay_rafidinjanahary_emmanuel.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.validation.MaxConfiguredTaille;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record PageRequestDTO(

        @Min(value = 1, message = "La page minimale est 1")
        Integer page,

        @Min(value = 1, message = "Valeur hors limites")
        @MaxConfiguredTaille
        Integer taille

) {
    public PageRequestDTO withDefaults(int defaultPage, int defaultTaille) {
        return new PageRequestDTO(
                this.page == null ? defaultPage : page,
                taille == null ? defaultTaille : taille
        );
    }
}