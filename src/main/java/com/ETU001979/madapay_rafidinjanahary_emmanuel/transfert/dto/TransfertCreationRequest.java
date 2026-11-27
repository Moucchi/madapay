package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.CorpsStrict;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransfertCreationRequest extends CorpsStrict {

    @NotNull(message = "Le compte source est obligatoire.")
    @JsonProperty("compte_source_id")
    private UUID compteSourceId;

    @NotNull(message = "Le bénéficiaire est obligatoire.")
    @JsonProperty("beneficiaire_id")
    private UUID beneficiaireId;

    @NotNull(message = "Le montant est obligatoire.")
    @Positive(message = "Le montant doit être un entier strictement positif.")
    private Long montant;

    @Size(max = 140, message = "Le motif ne doit pas dépasser 140 caractères.")
    private String motif;
}
