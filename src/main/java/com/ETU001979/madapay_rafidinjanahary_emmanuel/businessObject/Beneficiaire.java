package com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class Beneficiaire {
    UUID id;

    @JsonProperty("compte_id")
    UUID compteId;

    String nom;

    String telephone;

    @JsonProperty("cree_le")
    Instant creeLe;
}
