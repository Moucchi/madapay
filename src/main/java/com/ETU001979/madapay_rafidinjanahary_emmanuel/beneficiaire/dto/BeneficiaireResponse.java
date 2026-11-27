package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record BeneficiaireResponse(
        UUID id,
        @JsonProperty("compte_id") UUID compteId,
        String nom,
        String telephone,
        @JsonProperty("cree_le") Instant creeLe
) {
    public static BeneficiaireResponse from(Beneficiaire b) {
        return new BeneficiaireResponse(b.getId(), b.getCompteId(), b.getNom(), b.getTelephone(), b.getCreeLe());
    }
}
