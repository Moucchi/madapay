package com.ETU001979.madapay_rafidinjanahary_emmanuel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CompteDTO(
        String id,
        String telephone,
        String titulaire,
        Long solde,
        @JsonProperty("plafond_journalier") Long plafondJournalier,
        @JsonProperty("cree_le") Instant creeLe
) {
}
