package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository.seed;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SeedFichier(
        int version,
        @JsonProperty("plafond_journalier") long plafondJournalier,
        List<CompteSeed> comptes
) {
    public record CompteSeed(
            String cle,
            String titulaire,
            String telephone,
            @JsonProperty("code_pin") String codePin,
            Role role,
            long solde
    ) {
    }
}
