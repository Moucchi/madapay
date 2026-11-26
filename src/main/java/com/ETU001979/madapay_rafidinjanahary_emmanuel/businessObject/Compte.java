package com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compte {
    @JsonIgnore
    UUID id;

    @JsonProperty("telephone")
    String telephone;

    @JsonProperty("titulaire")
    String titulaire;

    @JsonProperty("cle")
    String cle;

    @JsonProperty("solde")
    long solde;

    @JsonProperty("code_pin")
    String codePin;

    @JsonProperty("role")
    Role role;

    @JsonIgnore
    long plafondJournalier;

    @JsonIgnore
    Instant creeLe;

    @JsonCreator
    public Compte(
            @JsonProperty("telephone") String telephone,
            @JsonProperty("titulaire") String titulaire,
            @JsonProperty("cle") String cle,
            @JsonProperty("solde") long solde,
            @JsonProperty("code_pin") String codePin,
            @JsonProperty("role") Role role
    ) {
        this.telephone = telephone;
        this.titulaire = titulaire;
        this.cle = cle;
        this.solde = solde;
        this.codePin = codePin;
        this.role = role;
    }
}
