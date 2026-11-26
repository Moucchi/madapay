package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/** Représentation JSON d'un compte (schéma Compte du contrat). Le code PIN n'est jamais exposé. */
public record CompteResponse(
        UUID id,
        String telephone,
        String titulaire,
        long solde,
        @JsonProperty("plafond_journalier") long plafondJournalier,
        @JsonProperty("cree_le") Instant creeLe
) {
    public static CompteResponse from(Compte compte) {
        return new CompteResponse(
                compte.getId(),
                compte.getTelephone(),
                compte.getTitulaire(),
                compte.getSolde(),
                compte.getPlafondJournalier(),
                compte.getCreeLe());
    }
}
