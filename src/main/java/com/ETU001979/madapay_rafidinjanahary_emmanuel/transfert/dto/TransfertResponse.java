package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.TransfertStatut;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransfertResponse(
        UUID id,
        @JsonProperty("compte_source_id") UUID compteSourceId,
        @JsonProperty("beneficiaire_id") UUID beneficiaireId,
        long montant,
        long frais,
        @JsonProperty("total_debite") long totalDebite,
        String motif,
        TransfertStatut statut,
        @JsonProperty("cree_le") Instant creeLe,
        @JsonProperty("mis_a_jour_le") Instant misAJourLe
) {
    public static TransfertResponse from(Transfert t) {
        return new TransfertResponse(t.getId(), t.getCompteSourceId(), t.getBeneficiaireId(),
                t.getMontant(), t.getFrais(), t.getTotalDebite(), t.getMotif(),
                t.getStatut(), t.getCreeLe(), t.getMisAJourLe());
    }
}
