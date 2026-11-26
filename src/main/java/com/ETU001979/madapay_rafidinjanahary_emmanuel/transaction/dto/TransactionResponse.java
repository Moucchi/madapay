package com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.TransfertStatut;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        @JsonProperty("compte_id") UUID compteId,
        @JsonProperty("transfert_id") UUID transfertId,
        Type type,
        long montant,
        TransfertStatut statut,
        Instant date
) {
    public enum Type {
        DEBIT, FRAIS
    }
}
